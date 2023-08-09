package org.matilda.di;

import dagger.Module;
import dagger.Provides;
import org.matilda.commands.CommandMessageHandler;
import org.matilda.messages.MessageHandlerRegistry;
import org.matilda.messages.handlers.MessageDispatcher;
import org.matilda.messages.handlers.MessageHandler;
import org.matilda.messages.protobuf.MessageType;

import javax.inject.Inject;
import java.util.concurrent.ExecutorService;

@Module(includes = ExecutorServiceModule.class)
public class MessageHandlerFactory {
    @Inject
    ExecutorService mExecutorService;

    @Inject
    MessageHandlerRegistry mMessageHandlerRegistry;

    @Inject
    CommandMessageHandler mCommandMessageHandler;

    @Provides
    MessageHandler create() {
        mMessageHandlerRegistry.registerHandler(MessageType.COMMAND.getNumber(), mCommandMessageHandler);

        return new MessageDispatcher(mExecutorService, mMessageHandlerRegistry);
    }
}
