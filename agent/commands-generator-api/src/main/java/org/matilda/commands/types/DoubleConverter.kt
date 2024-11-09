package org.matilda.commands.types

import com.google.protobuf.Any
import com.google.protobuf.DoubleValue

class DoubleConverter : ProtobufConverter<Double> {
    override fun convertToProtobuf(obj: Double): DoubleValue {
        return DoubleValue.newBuilder().setValue(obj).build()
    }

    override fun convertFromProtobuf(obj: Any): Double {
        return obj.unpack(DoubleValue::class.java).value
    }
}
