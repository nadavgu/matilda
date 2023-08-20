package org.matilda.commands.types;

import com.google.protobuf.Any;
import com.google.protobuf.BoolValue;
import com.google.protobuf.InvalidProtocolBufferException;

public class BooleanConverter implements ProtobufConverter<Boolean> {
    @Override
    public BoolValue convertToProtobuf(Boolean object) {
        return BoolValue.newBuilder().setValue(object).build();
    }

    @Override
    public Boolean convertFromProtobuf(Any object) throws InvalidProtocolBufferException {
        return object.unpack(BoolValue.class).getValue();
    }
}
