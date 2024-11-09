package org.matilda.commands

import dagger.Binds
import dagger.Module
import dagger.Provides
import org.matilda.generated.commands.CommandRegistryModule
import javax.inject.Named
import javax.inject.Singleton

@Module(includes = [CommandRegistryModule::class])
abstract class CommandsModule {
    @Binds
    abstract fun bindCommandRunner(commandRunner: CommunicationCommandRunner): CommandRunner
    @Binds
    abstract fun bindCommandRegistryManager(commandRepository: CommandRepository): CommandRegistryManager


    companion object {
        const val INITIALIZED_COMMAND_REPOSITORY_TAG = "dependency.initialized_command_repository"

        @Provides
        @Singleton
        @Named(INITIALIZED_COMMAND_REPOSITORY_TAG)
        fun initializedCommandRepository(commandRepository: CommandRepository,
                                         defaultCommandRegistry: CommandRegistry): CommandRepository {
            commandRepository.setDefaultCommandRegistry(defaultCommandRegistry)
            return commandRepository
        }
    }
}
