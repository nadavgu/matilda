package org.matilda.commands

import kotlinx.datetime.Clock
import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.Provides
import kotlin.random.Random

@Component
abstract class PluginDependenciesComponent(private val mPluginDependencies: PluginDependencies) {
    @Provides
    fun commandRegistryManager() = mPluginDependencies.commandRegistryManager

    @Provides
    fun commandRunner() = mPluginDependencies.commandRunner

    @Provides
    fun random() = Random(Clock.System.now().toEpochMilliseconds())
}
