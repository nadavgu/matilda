package org.matilda.services.plugins

import org.matilda.commands.CommandRegistry
import org.matilda.commands.PluginDependencies

class JavaPlugin(private val mClassLoader: ClassLoader) : Plugin {
    override fun createCommandRegistry(entryPoint: String, pluginDependencies: PluginDependencies): CommandRegistry {
        val entryPointClass = Class.forName(entryPoint, true, mClassLoader)
        val commandRegistryMethod = entryPointClass.getDeclaredMethod(
            "createCommandRegistry",
            PluginDependencies::class.java
        )
        return commandRegistryMethod.invoke(null, pluginDependencies) as CommandRegistry
    }
}