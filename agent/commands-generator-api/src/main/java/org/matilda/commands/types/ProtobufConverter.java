package org.matilda.commands.types;

import com.google.protobuf.Message;

public interface ProtobufConverter<T> {
    Message convert(T object);
}
