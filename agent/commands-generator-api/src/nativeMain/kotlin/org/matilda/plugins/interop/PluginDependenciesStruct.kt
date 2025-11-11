@file:OptIn(ExperimentalForeignApi::class)

package org.matilda.plugins.interop

import kotlinx.cinterop.COpaquePointer
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.cValue
import kotlinx.cinterop.pointed
import kotlinx.cinterop.reinterpret
import org.matilda.commands.PluginDependencies

fun PluginDependenciesStruct.toPluginDependencies() = PluginDependencies(
        commandRegistryManager = commandRegistryManager.toCommandRegistryManager(),
        commandRunner = commandRunner.toCommandRunner(),
    )

fun PluginDependencies.toPluginDependenciesStruct() = cValue<PluginDependenciesStruct> {
    commandRunner.initializeFrom(this@toPluginDependenciesStruct.commandRunner)
    commandRegistryManager.initializeFrom(this@toPluginDependenciesStruct.commandRegistryManager)
}

// This function is required to use COpaquePointer as plugins code cannot use the interop classes directly
fun COpaquePointer.toPluginDependencies() = reinterpret<PluginDependenciesStruct>().pointed.toPluginDependencies()
