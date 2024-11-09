package org.matilda.commands.types;

import com.google.protobuf.Any;
import com.google.protobuf.ByteString;
import com.google.protobuf.BytesValue;
import com.google.protobuf.InvalidProtocolBufferException;

public class ByteArrayConverter implements ProtobufConverter<byte[]> {
    private final ByteStringConverter mByteStringConverter;

    public ByteArrayConverter() {
        mByteStringConverter = new ByteStringConverter();
    }

    @Override
    public BytesValue convertToProtobuf(byte[] object) {
        return mByteStringConverter.convertToProtobuf(ByteString.copyFrom(object));
    }

    @Override
    public byte[] convertFromProtobuf(Any object) throws InvalidProtocolBufferException {
        return mByteStringConverter.convertFromProtobuf(object).toByteArray();
    }
}
