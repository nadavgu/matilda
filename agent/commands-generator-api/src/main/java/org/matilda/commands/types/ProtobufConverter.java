package org.matilda.commands.types;

import com.google.protobuf.Any;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;

public interface ProtobufConverter<T> {
    Message convertToProtobuf(T object);
    T convertFromProtobuf(Any object) throws InvalidProtocolBufferException;
}
