package org.matilda.services.plugins

import me.tatarka.inject.annotations.Inject
import org.matilda.commands.CommandRepository
import org.matilda.commands.MatildaCommand
import org.matilda.commands.MatildaService
import org.matilda.commands.PluginDependencies

@MatildaService
class PluginsService @Inject constructor(private val mCommandRepository: CommandRepository,
                                         private val mPluginDependencies: PluginDependencies) {
    @MatildaCommand
    fun loadPlugin(bytes: ByteArray, entryPoint: String): Int {
        val plugin = loadPlugin(bytes)
        val commandRegistry = plugin.createCommandRegistry(entryPoint, mPluginDependencies)
        return mCommandRepository.addCommandRegistry(commandRegistry)
    }
}
