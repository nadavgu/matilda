package org.matilda.commands

import dagger.Module
import dagger.Provides
import kotlinx.datetime.Clock
import kotlin.random.Random

@Module
class PluginDependenciesModule(private val mPluginDependencies: PluginDependencies) {
    @Provides
    fun commandRegistryManager() = mPluginDependencies.commandRegistryManager

    @Provides
    fun commandRunner() = mPluginDependencies.commandRunner

    @Provides
    fun random() = Random(Clock.System.now().toEpochMilliseconds())
}
