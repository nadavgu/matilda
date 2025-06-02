@file:OptIn(ExperimentalForeignApi::class)

package org.matilda.plugins.interop

import kotlinx.cinterop.*
import org.matilda.commands.CommandRegistry
import org.matilda.commands.CommandRegistryManager

class NativeCommandRegistryManager(private val mFunction: CPointer<CFunction<(COpaquePointer?, CValue<CommandRegistryStruct>) -> Int>>,
                                   private val mInstance: COpaquePointer) : CommandRegistryManager {
    override fun addCommandRegistry(commandRegistry: CommandRegistry): Int {
        return mFunction(mInstance, commandRegistry.toCommandRegistryStruct().readValue())
    }
}