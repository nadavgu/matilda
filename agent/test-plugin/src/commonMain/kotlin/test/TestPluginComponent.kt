package test

import me.tatarka.inject.annotations.Component
import org.matilda.commands.CommandRegistry
import org.matilda.commands.MatildaScope
import org.matilda.commands.PluginDependenciesComponent
import org.matilda.template.generated.commands.CommandRegistryModule

@Component
@MatildaScope
abstract class TestPluginComponent(@Component val pluginDependencies: PluginDependenciesComponent) : CommandRegistryModule {
    abstract fun commandRegistry(): CommandRegistry
}
