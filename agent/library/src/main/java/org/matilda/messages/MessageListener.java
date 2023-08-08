package org.matilda.messages;

import org.matilda.messages.handlers.MessageHandler;

import javax.inject.Inject;
import java.io.EOFException;
import java.io.IOException;

public class MessageListener {
    @Inject
    MessageReceiver mMessageReceiver;

    @Inject
    MessageHandler mMessageHandler;

    @Inject
    public MessageListener() {}

    public void start() throws IOException {
        while (true) {
            try {
                Message message = mMessageReceiver.receive();
                mMessageHandler.handle(message);
            } catch (EOFException ignored) {
                return;
            }
        }
    }
}
