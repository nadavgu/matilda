package org.matilda.commands.types

import com.google.protobuf.Any
import com.google.protobuf.Empty

class EmptyConverter : ProtobufConverter<Void?> {
    override fun convertToProtobuf(obj: Void?): Empty {
        return Empty.newBuilder().build()
    }

    override fun convertFromProtobuf(obj: Any): Void? {
        return null
    }
}
