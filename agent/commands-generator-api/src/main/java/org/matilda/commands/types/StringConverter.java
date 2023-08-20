package org.matilda.commands.types;

import com.google.protobuf.StringValue;

public class StringConverter implements ProtobufConverter<String> {
    @Override
    public StringValue convert(String object) {
        return StringValue.newBuilder().setValue(object).build();
    }
}
