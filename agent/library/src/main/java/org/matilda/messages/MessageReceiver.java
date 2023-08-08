package org.matilda.messages;

import java.io.IOException;

public interface MessageReceiver {
    Message receive() throws IOException;
}
