package org.matilda.commands.types

import com.google.protobuf.Any
import com.google.protobuf.Int64Value

class LongConverter : ProtobufConverter<Long> {
    override fun convertToProtobuf(obj: Long): Int64Value {
        return Int64Value.newBuilder().setValue(obj).build()
    }

    override fun convertFromProtobuf(obj: Any): Long {
        return obj.unpack(Int64Value::class.java).value
    }
}
