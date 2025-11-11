@file:OptIn(ExperimentalForeignApi::class)
package org.matilda.plugins.interop

import kotlinx.cinterop.*
import org.matilda.commands.Command
import org.matilda.commands.CommandRegistry

fun CommandRegistryStruct.toCommandRegistry(): CommandRegistry {
    val commandRegistry = CommandRegistry()
    val commandRegistrySize = getSize!!(handle)

    memScoped {
        val commandRegistryTypes = allocArray<IntVar>(commandRegistrySize)
        val commandPointers = allocArray<COpaquePointerVar>(commandRegistrySize)

        getCommands!!(handle, commandRegistryTypes, commandPointers)

        for (i in 0 until commandRegistrySize) {
            commandRegistry.addCommand(commandRegistryTypes[i], NativeCommand(runCommand!!, commandPointers[i]!!))
        }
    }

    return commandRegistry
}

fun CommandRegistryStruct.close() {
    free!!(this.ptr)
}

fun <R> CommandRegistryStruct.use(block: CommandRegistryStruct.() -> R): R = try {
    block()
} finally {
    close()
}

fun CommandRegistryStruct.moveToCommandRegistry() = use {
    toCommandRegistry()
}

fun CommandRegistry.toCommandRegistryStruct() = nativeHeap.alloc<CommandRegistryStruct> {
    val stableRef = StableRef.create(this@toCommandRegistryStruct)
    handle = stableRef.asCPointer()

    getSize = staticCFunction { handle ->
        handle!!.asStableRef<CommandRegistry>().get().commands.size
    }

    getCommands = staticCFunction { handle, outTypes, outCommands ->
        handle!!.asStableRef<CommandRegistry>().get().commands.onEachIndexed { index, (type, command) ->
            outTypes!![index] = type
            outCommands!![index] = StableRef.create(command).asCPointer()
        }
    }


    runCommand = staticCFunction { commandHandle, parameterStruct ->
        commandHandle!!.asStableRef<Command>().get().runCatching(parameterStruct.toByteArray()).toCommandResultStruct()
    }

    free = staticCFunction { ptr ->
        ptr?.pointed?.handle?.asStableRef<CommandRegistry>()?.dispose()
        ptr?.pointed?.handle = null
        ptr?.apply {
            nativeHeap.free(pointed)
        }
    }
}

fun <R> CommandRegistry.tempCommandRegistryStruct(block: (CommandRegistryStruct) -> R): R = toCommandRegistryStruct().use(block)

// This function is required to use COpaquePointer as plugins code cannot use the interop classes directly
fun CommandRegistry.toCommandRegistryStructPtr(): COpaquePointer = toCommandRegistryStruct().ptr