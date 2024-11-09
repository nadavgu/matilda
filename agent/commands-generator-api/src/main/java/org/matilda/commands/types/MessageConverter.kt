package org.matilda.commands.types

import com.google.protobuf.Any
import com.google.protobuf.Message

class MessageConverter<T : Message>(private val mClass: Class<T>) : ProtobufConverter<T> {
    override fun convertToProtobuf(obj: T): T {
        return obj
    }

    override fun convertFromProtobuf(obj: Any): T {
        return obj.unpack(mClass)
    }
}
