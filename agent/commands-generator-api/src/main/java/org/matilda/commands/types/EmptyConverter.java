package org.matilda.commands.types;

import com.google.protobuf.Any;
import com.google.protobuf.Empty;
import com.google.protobuf.InvalidProtocolBufferException;

public class EmptyConverter implements ProtobufConverter<Void> {
    @Override
    public Empty convertToProtobuf(Void object) {
        return Empty.newBuilder().build();
    }

    @Override
    public Void convertFromProtobuf(Any object) throws InvalidProtocolBufferException {
        return null;
    }
}
