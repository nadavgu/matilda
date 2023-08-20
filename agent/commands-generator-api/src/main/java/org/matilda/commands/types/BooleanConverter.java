package org.matilda.commands.types;

import com.google.protobuf.BoolValue;

public class BooleanConverter implements ProtobufConverter<Boolean> {
    @Override
    public BoolValue convert(Boolean object) {
        return BoolValue.newBuilder().setValue(object).build();
    }
}
