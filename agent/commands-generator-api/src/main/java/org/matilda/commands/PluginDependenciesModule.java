package org.matilda.commands;

import dagger.Module;
import dagger.Provides;

@Module
public class PluginDependenciesModule {
    private final PluginDependencies mPluginDependencies;

    public PluginDependenciesModule(PluginDependencies pluginDependencies) {
        mPluginDependencies = pluginDependencies;
    }

    @Provides
    CommandRegistryManager commandRegistryManager() {
        return mPluginDependencies.commandRegistryManager;
    }

    @Provides
    CommandRunner commandRunner() {
        return mPluginDependencies.commandRunner;
    }
}
