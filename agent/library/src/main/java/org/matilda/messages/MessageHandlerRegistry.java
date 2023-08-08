package org.matilda.messages;

import org.matilda.messages.handlers.MessageHandler;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Singleton
public class MessageHandlerRegistry implements MessageHandler {
    private final Map<Integer, List<MessageHandler>> mMessageHandlers;

    @Inject
    public MessageHandlerRegistry() {
        mMessageHandlers = new HashMap<>();
    }

    public void registerHandler(int type, MessageHandler handler) {
        if (!mMessageHandlers.containsKey(type)) {
            mMessageHandlers.put(type, new ArrayList<>());
        }

        mMessageHandlers.get(type).add(handler);
    }

    @Override
    public void handle(Message message) {
        if (mMessageHandlers.containsKey(message.type)) {
            mMessageHandlers.get(message.type).forEach(handler -> handler.handle(message));
        }
    }
}
