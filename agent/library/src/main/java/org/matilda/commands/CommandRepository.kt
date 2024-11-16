package org.matilda.commands

import me.tatarka.inject.annotations.Inject

@MatildaScope
@Inject
class CommandRepository(private val mCommandRegistryIdGenerator: CommandRegistryIdGenerator) : CommandRegistryManager {
    private val mCommandsRegistries = mutableMapOf<Int, CommandRegistry>()

    override fun addCommandRegistry(commandRegistry: CommandRegistry) = mCommandRegistryIdGenerator.generate().also {
        mCommandsRegistries[it] = commandRegistry
    }

    fun setDefaultCommandRegistry(commandRegistry: CommandRegistry) {
        mCommandsRegistries[DEFAULT_PROVIDER_ID] = commandRegistry
    }

    private fun getCommandRegistry(registryId: Int) = mCommandsRegistries.getOrElse(registryId) {
        throw IllegalArgumentException("Command Provider $registryId not found")
    }

    fun getCommand(registryId: Int, commandType: Int): Command {
        return getCommandRegistry(registryId)[commandType]
    }

    companion object {
        private const val DEFAULT_PROVIDER_ID = 0
    }
}
