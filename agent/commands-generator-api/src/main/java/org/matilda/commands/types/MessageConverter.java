package org.matilda.commands.types;

import com.google.protobuf.Message;

public class MessageConverter implements ProtobufConverter<Message> {
    @Override
    public Message convert(Message object) {
        return object;
    }
}
