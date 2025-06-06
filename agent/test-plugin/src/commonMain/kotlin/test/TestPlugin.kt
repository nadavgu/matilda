package test

import org.matilda.commands.CommandRegistry
import org.matilda.commands.PluginDependencies
import org.matilda.commands.PluginDependenciesComponent
import org.matilda.commands.create
import kotlin.jvm.JvmStatic

object TestPlugin {
    @JvmStatic
    fun createCommandRegistry(pluginDependencies: PluginDependencies): CommandRegistry {
        return TestPluginComponent::class.create(PluginDependenciesComponent::class.create(pluginDependencies))
            .commandRegistry()
    }
}
