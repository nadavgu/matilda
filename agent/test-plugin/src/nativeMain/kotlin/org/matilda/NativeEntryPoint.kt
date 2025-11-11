@file:OptIn(ExperimentalNativeApi::class, ExperimentalForeignApi::class)

package org.matilda

import kotlinx.cinterop.*
import org.matilda.plugins.interop.*
import test.TestPlugin
import kotlin.experimental.ExperimentalNativeApi

@CName(externName = "createCommandRegistry")
fun createCommandRegistry(pluginDependencies: COpaquePointer): COpaquePointer {
    val commandRegistry = TestPlugin.createCommandRegistry(pluginDependencies.toPluginDependencies())
    return commandRegistry.toCommandRegistryStructPtr()
}
