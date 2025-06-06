package test;

import org.matilda.commands.CommandRegistry;
import org.matilda.commands.PluginDependencies;
import org.matilda.commands.PluginDependenciesModule;

public class JavaTestPlugin {
    public static CommandRegistry createCommandRegistry(PluginDependencies pluginDependencies) {
        return DaggerJavaTestPluginComponent.builder()
                .pluginDependenciesModule(new PluginDependenciesModule(pluginDependencies))
                .build()
                .commandRegistry();
    }
}
