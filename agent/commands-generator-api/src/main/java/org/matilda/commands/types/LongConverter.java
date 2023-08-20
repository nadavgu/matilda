package org.matilda.commands.types;

import com.google.protobuf.Any;
import com.google.protobuf.Int64Value;
import com.google.protobuf.InvalidProtocolBufferException;

public class LongConverter implements ProtobufConverter<Long> {
    @Override
    public Int64Value convertToProtobuf(Long object) {
        return Int64Value.newBuilder().setValue(object).build();
    }

    @Override
    public Long convertFromProtobuf(Any object) throws InvalidProtocolBufferException {
        return object.unpack(Int64Value.class).getValue();
    }
}
