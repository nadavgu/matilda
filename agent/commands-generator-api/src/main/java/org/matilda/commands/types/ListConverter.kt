package org.matilda.commands.types;

import com.google.protobuf.Any;
import com.google.protobuf.InvalidProtocolBufferException;
import org.matilda.commands.protobuf.Some;

import java.util.ArrayList;
import java.util.List;

public class ListConverter<T> implements ProtobufConverter<List<T>> {
    private final ProtobufConverter<T> mInternalConverter;

    public ListConverter(ProtobufConverter<T> internalConverter) {
        mInternalConverter = internalConverter;
    }

    @Override
    public Some convertToProtobuf(List<T> list) {
        Some.Builder builder = Some.newBuilder();
        list.forEach(element -> builder.addAny(Any.pack(mInternalConverter.convertToProtobuf(element))));
        return builder.build();
    }

    @Override
    public List<T> convertFromProtobuf(Any object) throws InvalidProtocolBufferException {
        Some some = object.unpack(Some.class);
        List<T> list = new ArrayList<>();
        for (Any any : some.getAnyList()) {
            list.add(mInternalConverter.convertFromProtobuf(any));
        }

        return list;
    }
}
