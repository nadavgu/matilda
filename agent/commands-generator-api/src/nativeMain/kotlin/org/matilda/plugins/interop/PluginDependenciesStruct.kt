@file:OptIn(ExperimentalForeignApi::class)

package org.matilda.plugins.interop

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.cValue
import org.matilda.commands.PluginDependencies

fun PluginDependenciesStruct.toPluginDependencies() = PluginDependencies(
        commandRegistryManager = commandRegistryManager.toCommandRegistryManager(),
        commandRunner = commandRunner.toCommandRunner(),
    )

fun PluginDependencies.toPluginDependenciesStruct() = cValue<PluginDependenciesStruct> {
    commandRunner.initializeFrom(this@toPluginDependenciesStruct.commandRunner)
    commandRegistryManager.initializeFrom(this@toPluginDependenciesStruct.commandRegistryManager)
}