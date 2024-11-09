package org.matilda.commands.types

import com.google.protobuf.Any
import com.google.protobuf.InvalidProtocolBufferException
import com.google.protobuf.Message

interface ProtobufConverter<T> {
    fun convertToProtobuf(obj: T): Message

    @Throws(InvalidProtocolBufferException::class)
    fun convertFromProtobuf(obj: Any): T
}
