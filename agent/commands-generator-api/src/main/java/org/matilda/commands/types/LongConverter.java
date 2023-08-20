package org.matilda.commands.types;

import com.google.protobuf.Int64Value;

public class LongConverter implements ProtobufConverter<Long> {
    @Override
    public Int64Value convert(Long object) {
        return Int64Value.newBuilder().setValue(object).build();
    }
}
