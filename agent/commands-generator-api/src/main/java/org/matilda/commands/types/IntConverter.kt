package org.matilda.commands.types

import com.google.protobuf.Any
import com.google.protobuf.Int32Value

class IntConverter : ProtobufConverter<Int> {
    override fun convertToProtobuf(obj: Int): Int32Value {
        return Int32Value.newBuilder().setValue(obj).build()
    }

    override fun convertFromProtobuf(obj: Any): Int {
        return obj.unpack(Int32Value::class.java).value
    }
}
