package org.matilda.messages;

import org.matilda.messages.handlers.MessageHandler;

public class MessageHandlerRegistration {
    private final MessageHandlerRegistry mMessageHandlerRegistry;
    private final int mMessageType;
    private final MessageHandler mMessageHandler;

    public MessageHandlerRegistration(MessageHandlerRegistry messageHandlerRegistry, int messageType,
                                      MessageHandler messageHandler) {
        mMessageHandlerRegistry = messageHandlerRegistry;
        mMessageType = messageType;
        mMessageHandler = messageHandler;
    }

    public void unregister() {
        mMessageHandlerRegistry.unregisterHandler(mMessageType, mMessageHandler);
    }
}
