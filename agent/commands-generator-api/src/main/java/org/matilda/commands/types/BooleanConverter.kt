package org.matilda.commands.types

import com.google.protobuf.Any
import com.google.protobuf.BoolValue

class BooleanConverter : ProtobufConverter<Boolean> {
    override fun convertToProtobuf(obj: Boolean): BoolValue {
        return BoolValue.newBuilder().setValue(obj).build()
    }

    override fun convertFromProtobuf(obj: Any): Boolean {
        return obj.unpack(BoolValue::class.java).value
    }
}
