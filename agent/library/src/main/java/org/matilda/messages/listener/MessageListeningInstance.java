package org.matilda.messages.listener;

import org.matilda.messages.Message;
import org.matilda.messages.MessageHandlerRegistration;

import java.util.concurrent.BlockingQueue;

public class MessageListeningInstance implements AutoCloseable {
    private final BlockingQueue<Message> mMessageQueue;
    private final MessageHandlerRegistration mRegistration;

    public MessageListeningInstance(BlockingQueue<Message> messageQueue, MessageHandlerRegistration registration) {
        mMessageQueue = messageQueue;
        mRegistration = registration;
    }

    public Message waitForMessage() throws InterruptedException {
        return mMessageQueue.take();
    }

    public void stop() {
        mRegistration.unregister();
    }

    public void close() {
        stop();
    }
}
