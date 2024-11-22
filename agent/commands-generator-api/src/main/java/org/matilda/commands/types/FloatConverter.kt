package org.matilda.commands.types

import pbandk.unpack
import pbandk.wkt.Any
import pbandk.wkt.FloatValue

class FloatConverter : ProtobufConverter<Float> {
    override fun convertToProtobuf(obj: Float): FloatValue {
        return FloatValue(obj)
    }

    override fun convertFromProtobuf(obj: Any): Float {
        return obj.unpack(FloatValue.Companion).value
    }
}
