package org.matilda.commands

import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.Provides

@Component
abstract class PluginDependenciesComponent(private val mPluginDependencies: PluginDependencies) {
    @Provides
    fun commandRegistryManager() = mPluginDependencies.commandRegistryManager

    @Provides
    fun commandRunner() = mPluginDependencies.commandRunner

    @Provides
    fun random() = mPluginDependencies.random
}
