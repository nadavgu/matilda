package org.matilda.messages;

import javax.inject.Inject;
import java.io.EOFException;
import java.io.IOException;

public class MessageDispatcher {
    @Inject
    MessageReceiver mMessageReceiver;

    @Inject
    public MessageDispatcher() {}

    public void start() throws IOException {
        while (true) {
            try {
                Message message = mMessageReceiver.receive();
                System.out.printf("%d %s\n", message.type, new String(message.data));
            } catch (EOFException ignored) {
                return;
            }
        }
    }
}
