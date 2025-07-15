package org.matilda.commands

import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.Provides
import me.tatarka.inject.annotations.Qualifier
import org.matilda.generated.commands.CommandRegistryModule

@Qualifier
annotation class InitializedCommandRepository

@Component
@MatildaScope
interface CommandsComponent : CommandRegistryModule {
    @Provides
    fun bindCommandRunner(commandRunner: CommunicationCommandRunner): CommandRunner = commandRunner
    @Provides
    fun bindCommandRegistryManager(commandRepository: CommandRepository): CommandRegistryManager = commandRepository

    @Provides
    @MatildaScope
    @InitializedCommandRepository
    fun initializedCommandRepository(commandRepository: CommandRepository,
                                     defaultCommandRegistry: CommandRegistry): CommandRepository {
        commandRepository.setDefaultCommandRegistry(defaultCommandRegistry)
        return commandRepository
    }
}
