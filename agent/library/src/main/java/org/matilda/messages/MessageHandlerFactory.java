package org.matilda.messages;

import org.matilda.commands.CommandMessageHandler;
import org.matilda.messages.handlers.MessageDispatcher;
import org.matilda.messages.handlers.MessageHandler;
import org.matilda.messages.protobuf.MessageType;

import javax.inject.Inject;
import java.util.concurrent.ExecutorService;

public class MessageHandlerFactory {
    @Inject
    ExecutorService mExecutorService;

    @Inject
    MessageHandlerRegistry mMessageHandlerRegistry;

    @Inject
    CommandMessageHandler mCommandMessageHandler;

    @Inject
    public MessageHandlerFactory() {}

    public MessageHandler create() {
        mMessageHandlerRegistry.registerHandler(MessageType.COMMAND.getNumber(), mCommandMessageHandler);

        return new MessageDispatcher(mExecutorService, mMessageHandlerRegistry);
    }
}
