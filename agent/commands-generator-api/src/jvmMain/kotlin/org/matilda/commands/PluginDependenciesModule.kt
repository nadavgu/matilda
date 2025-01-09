package org.matilda.commands

import dagger.Module
import dagger.Provides

@Module
class PluginDependenciesModule(private val mPluginDependencies: PluginDependencies) {
    @Provides
    fun commandRegistryManager() = mPluginDependencies.commandRegistryManager

    @Provides
    fun commandRunner() = mPluginDependencies.commandRunner

    @Provides
    fun random() = mPluginDependencies.random
}
