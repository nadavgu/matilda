package org.matilda.commands.types;

import com.google.protobuf.Any;
import com.google.protobuf.DoubleValue;
import com.google.protobuf.InvalidProtocolBufferException;

public class DoubleConverter implements ProtobufConverter<Double> {
    @Override
    public DoubleValue convertToProtobuf(Double object) {
        return DoubleValue.newBuilder().setValue(object).build();
    }

    @Override
    public Double convertFromProtobuf(Any object) throws InvalidProtocolBufferException {
        return object.unpack(DoubleValue.class).getValue();
    }
}
