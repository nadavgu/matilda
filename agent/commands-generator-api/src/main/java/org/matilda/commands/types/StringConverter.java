package org.matilda.commands.types;

import com.google.protobuf.Any;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.StringValue;

public class StringConverter implements ProtobufConverter<String> {
    @Override
    public StringValue convertToProtobuf(String object) {
        return StringValue.newBuilder().setValue(object).build();
    }

    @Override
    public String convertFromProtobuf(Any object) throws InvalidProtocolBufferException {
        return object.unpack(StringValue.class).getValue();
    }
}
