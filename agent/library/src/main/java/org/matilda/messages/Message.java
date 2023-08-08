package org.matilda.messages;

public class Message {
    public final int type;
    public final byte[] data;

    public Message(int type, byte[] data) {
        this.type = type;
        this.data = data;
    }
}
