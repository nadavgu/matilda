package test;

import dagger.Component;
import org.matilda.commands.CommandRegistry;
import org.matilda.commands.PluginDependenciesModule;
import test.generated.commands.CommandRegistryModule;

import javax.inject.Singleton;

@Component(modules = {CommandRegistryModule.class, PluginDependenciesModule.class})
@Singleton
public interface JavaTestPluginComponent {
    CommandRegistry commandRegistry();
}
