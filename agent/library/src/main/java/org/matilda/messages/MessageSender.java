package org.matilda.messages;

import java.io.IOException;

public interface MessageSender {
    void send(Message message) throws IOException;
}
