@file:OptIn(ExperimentalForeignApi::class)

package org.matilda.services.plugins

import kotlinx.cinterop.*
import org.matilda.commands.CommandRegistry
import org.matilda.commands.PluginDependencies
import org.matilda.plugins.interop.CommandRegistryStruct
import org.matilda.plugins.interop.PluginDependenciesStruct
import org.matilda.plugins.interop.moveToCommandRegistry
import org.matilda.plugins.interop.toPluginDependenciesStruct

class NativePlugin(private val mLibrary: NativeLibrary) : Plugin {
    override fun createCommandRegistry(entryPoint: String, pluginDependencies: PluginDependencies): CommandRegistry {
        val createCommandRegistryFunction =
            mLibrary.findFunction<(CValuesRef<PluginDependenciesStruct>) -> CPointer<CommandRegistryStruct>>(entryPoint)
        val commandRegistryPtr = createCommandRegistryFunction(pluginDependencies.toPluginDependenciesStruct())
        return commandRegistryPtr.pointed.moveToCommandRegistry()
    }
}
