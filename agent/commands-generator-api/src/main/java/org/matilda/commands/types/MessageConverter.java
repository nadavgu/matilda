package org.matilda.commands.types;

import com.google.protobuf.Any;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;

public class MessageConverter<T extends Message> implements ProtobufConverter<T> {
    private final Class<T> mClass;

    public MessageConverter(Class<T> clazz) {
        mClass = clazz;
    }

    @Override
    public T convertToProtobuf(T object) {
        return object;
    }

    @Override
    public T convertFromProtobuf(Any object) throws InvalidProtocolBufferException {
        return object.unpack(mClass);
    }
}
