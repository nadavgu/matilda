package org.matilda.commands.types;

import com.google.protobuf.*;

public class ByteStringConverter implements ProtobufConverter<ByteString> {
    @Override
    public BytesValue convertToProtobuf(ByteString object) {
        return BytesValue.newBuilder().setValue(object).build();
    }

    @Override
    public ByteString convertFromProtobuf(Any object) throws InvalidProtocolBufferException {
        return object.unpack(BytesValue.class).getValue();
    }
}
