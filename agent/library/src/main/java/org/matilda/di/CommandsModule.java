package org.matilda.di;

import dagger.Module;
import dagger.Provides;
import org.matilda.commands.CommandRegistry;
import org.matilda.commands.protobuf.CommandType;
import org.matilda.generated.commands.raw.MathServiceSquareCommand;

import javax.inject.Singleton;

@Module
public class CommandsModule {
    @Provides
    @Singleton
    CommandRegistry commandRegistry(MathServiceSquareCommand squareCommand) {
        CommandRegistry commandRegistry = new CommandRegistry();
        commandRegistry.addCommand(CommandType.ECHO_VALUE, squareCommand);
        return commandRegistry;
    }
}
