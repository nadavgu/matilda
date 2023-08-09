package org.matilda.di;

import dagger.Module;
import dagger.Provides;
import org.matilda.commands.CommandRegistry;
import org.matilda.commands.EchoCommand;
import org.matilda.commands.protobuf.CommandType;

import javax.inject.Singleton;

@Module
public class CommandsModule {
    @Provides
    @Singleton
    CommandRegistry commandRegistry() {
        CommandRegistry commandRegistry = new CommandRegistry();
        commandRegistry.addCommand(CommandType.ECHO_VALUE, new EchoCommand());
        return commandRegistry;
    }
}
