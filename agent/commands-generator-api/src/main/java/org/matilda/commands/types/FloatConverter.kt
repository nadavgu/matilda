package org.matilda.commands.types

import com.google.protobuf.Any
import com.google.protobuf.FloatValue

class FloatConverter : ProtobufConverter<Float> {
    override fun convertToProtobuf(obj: Float): FloatValue {
        return FloatValue.newBuilder().setValue(obj).build()
    }

    override fun convertFromProtobuf(obj: Any): Float {
        return obj.unpack(FloatValue::class.java).value
    }
}
