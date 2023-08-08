package org.matilda.messages;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import org.matilda.messages.protobuf.ProtobufMessage;

import javax.inject.Inject;
import java.io.InputStream;


public class ProtobufMessageSerializer implements MessageSerializer {
    @Inject
    public ProtobufMessageSerializer() {}
    @Inject
    InputStream mInputStream;

    @Override
    public byte[] serialize(Message message) {
        ProtobufMessage protobufMessage = ProtobufMessage.newBuilder()
                .setType(message.type)
                .setData(ByteString.copyFrom(message.data))
                .build();
        return protobufMessage.toByteArray();
    }

    @Override
    public Message deserialize(byte[] data) throws InvalidProtocolBufferException {
        ProtobufMessage protobufMessage = ProtobufMessage.parseFrom(data);
        return new Message(protobufMessage.getType(), protobufMessage.getData().toByteArray());
    }
}
