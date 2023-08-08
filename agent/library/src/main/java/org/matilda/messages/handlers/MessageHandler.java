package org.matilda.messages.handlers;

import org.matilda.messages.Message;

public interface MessageHandler {
    void handle(Message message);
}
