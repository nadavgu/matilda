package org.matilda.commands;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import org.matilda.generated.commands.CommandRegistryModule;

import javax.inject.Named;
import javax.inject.Singleton;

@Module(includes = CommandRegistryModule.class)
public abstract class CommandsModule {
    public static final String INITIALIZED_COMMAND_REPOSITORY_TAG = "dependency.initialized_command_repository";

    @Provides
    @Singleton
    @Named(INITIALIZED_COMMAND_REPOSITORY_TAG)
    static CommandRepository initializedCommandRepository(CommandRepository commandRepository,
                                                   CommandRegistry defaultCommandRegistry) {
        commandRepository.setDefaultCommandRegistry(defaultCommandRegistry);
        return commandRepository;
    }

    @Binds
    abstract CommandRunnerInterface bindCommandRunner(CommunicationCommandRunner commandRunner);
}
