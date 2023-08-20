package org.matilda.commands.types;

import com.google.protobuf.DoubleValue;

public class DoubleConverter implements ProtobufConverter<Double> {
    @Override
    public DoubleValue convert(Double object) {
        return DoubleValue.newBuilder().setValue(object).build();
    }
}
