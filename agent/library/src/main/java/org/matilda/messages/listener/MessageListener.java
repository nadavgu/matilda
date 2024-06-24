package org.matilda.messages.listener;


import org.matilda.messages.Message;
import org.matilda.messages.MessageHandlerRegistration;
import org.matilda.messages.MessageHandlerRegistry;

import javax.inject.Inject;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Predicate;


public class MessageListener {
    @Inject
    MessageHandlerRegistry mMessageHandlerRegistry;
    
    @Inject
    MessageListener() {}

    public MessageListeningInstance listen(int messageType, Predicate<Message> predicate) {
        BlockingQueue<Message> queue = new LinkedBlockingQueue<>();
        MessageHandlerRegistration registration = mMessageHandlerRegistry.registerHandler(messageType, message -> {
            if (predicate == null || predicate.test(message)) {
                queue.add(message);
            }
        });

        return new MessageListeningInstance(queue, registration);
    }

    public MessageListeningInstance listen(int messageType) {
        return listen(messageType, null);
    }
}
