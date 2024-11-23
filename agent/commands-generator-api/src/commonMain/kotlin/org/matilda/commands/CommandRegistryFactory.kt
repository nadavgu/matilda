package org.matilda.commands

interface CommandRegistryFactory<T> {
    fun createCommandRegistry(service: T): CommandRegistry
}
