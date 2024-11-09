package org.matilda.commands.types;

import com.google.protobuf.Any;
import com.google.protobuf.FloatValue;
import com.google.protobuf.InvalidProtocolBufferException;

public class FloatConverter implements ProtobufConverter<Float> {
    @Override
    public FloatValue convertToProtobuf(Float object) {
        return FloatValue.newBuilder().setValue(object).build();
    }

    @Override
    public Float convertFromProtobuf(Any object) throws InvalidProtocolBufferException {
        return object.unpack(FloatValue.class).getValue();
    }
}
