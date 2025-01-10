package org.matilda.services.plugins

import org.matilda.commands.CommandRegistry
import org.matilda.commands.PluginDependencies

interface Plugin {
    fun createCommandRegistry(entryPoint: String, pluginDependencies: PluginDependencies): CommandRegistry
}