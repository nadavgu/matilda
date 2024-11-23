package org.matilda.commands.types

import pbandk.unpack
import pbandk.wkt.Any
import pbandk.wkt.DoubleValue

class DoubleConverter : ProtobufConverter<Double> {
    override fun convertToProtobuf(obj: Double): DoubleValue {
        return DoubleValue(obj)
    }

    override fun convertFromProtobuf(obj: Any): Double {
        return obj.unpack(DoubleValue.Companion).value
    }
}
