package org.matilda.commands.types;

import com.google.protobuf.Int32Value;

public class IntConverter implements ProtobufConverter<Integer> {
    @Override
    public Int32Value convert(Integer object) {
        return Int32Value.newBuilder().setValue(object).build();
    }
}
