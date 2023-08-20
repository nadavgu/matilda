package org.matilda.commands.types;

import com.google.protobuf.FloatValue;

public class FloatConverter implements ProtobufConverter<Float> {
    @Override
    public FloatValue convert(Float object) {
        return FloatValue.newBuilder().setValue(object).build();
    }
}
