package org.matilda.commands;

import dagger.Module;
import dagger.Provides;

import java.util.Random;

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

    @Provides
    Random random() {
        return mPluginDependencies.random;
    }
}
