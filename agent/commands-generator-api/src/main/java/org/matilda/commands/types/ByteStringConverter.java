package org.matilda.commands.types;

import com.google.protobuf.ByteString;
import com.google.protobuf.BytesValue;

public class ByteStringConverter implements ProtobufConverter<ByteString> {
    @Override
    public BytesValue convert(ByteString object) {
        return BytesValue.newBuilder().setValue(object).build();
    }
}
