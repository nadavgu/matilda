@file:OptIn(ExperimentalForeignApi::class)

package org.matilda.services.plugins

import kotlinx.cinterop.CPointed
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.ExperimentalForeignApi
import org.matilda.commands.CommandRegistry
import org.matilda.commands.PluginDependencies

class NativePlugin(private val mHandle : CPointer<out CPointed>) : Plugin {
    override fun createCommandRegistry(entryPoint: String, pluginDependencies: PluginDependencies): CommandRegistry {
        TODO("Not yet implemented")
    }
}
