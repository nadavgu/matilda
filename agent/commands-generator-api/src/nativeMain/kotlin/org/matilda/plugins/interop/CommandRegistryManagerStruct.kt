@file:OptIn(ExperimentalForeignApi::class)
package org.matilda.plugins.interop

import kotlinx.cinterop.*
import org.matilda.commands.CommandRegistryManager

fun CommandRegistryManagerStruct.toCommandRegistryManager() =
    NativeCommandRegistryManager(addCommandRegistry!!, handle!!)


fun CommandRegistryManagerStruct.initializeFrom(commandRegistryManager: CommandRegistryManager) {
    val stableRef = StableRef.create(commandRegistryManager)
    handle = stableRef.asCPointer()

    addCommandRegistry = staticCFunction { handle, commandRegistryStruct ->
        commandRegistryStruct.useContents {
            handle!!.asStableRef<CommandRegistryManager>().get().addCommandRegistry(toCommandRegistry())
        }
    }
}
