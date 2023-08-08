package org.matilda.messages;

import java.io.IOException;

public interface MessageSerializer {
    byte[] serialize(Message message) throws IOException;
    Message deserialize(byte[] data) throws IOException;
}
