package org.matilda.commands.types;

import com.google.protobuf.Any;
import com.google.protobuf.Int32Value;
import com.google.protobuf.InvalidProtocolBufferException;

public class IntConverter implements ProtobufConverter<Integer> {
    @Override
    public Int32Value convertToProtobuf(Integer object) {
        return Int32Value.newBuilder().setValue(object).build();
    }

    @Override
    public Integer convertFromProtobuf(Any object) throws InvalidProtocolBufferException {
        return object.unpack(Int32Value.class).getValue();
    }
}
