package org.matilda.commands

interface CommandRegistryManager {
    fun addCommandRegistry(commandRegistry: CommandRegistry): Int
}
