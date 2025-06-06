@file:OptIn(ExperimentalNativeApi::class, ExperimentalForeignApi::class)

package org.matilda

import kotlinx.cinterop.*
import org.matilda.plugins.interop.*
import test.TestPlugin
import kotlin.experimental.ExperimentalNativeApi

@CName(externName = "createCommandRegistry")
fun createCommandRegistry(pluginDependencies: CPointer<PluginDependenciesStruct>): CPointer<CommandRegistryStruct> {
    val commandRegistry = TestPlugin.createCommandRegistry(pluginDependencies.pointed.toPluginDependencies())
    return commandRegistry.toCommandRegistryStruct().ptr
}
