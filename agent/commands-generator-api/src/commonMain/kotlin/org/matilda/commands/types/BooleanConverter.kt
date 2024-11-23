package org.matilda.commands.types

import pbandk.unpack
import pbandk.wkt.Any
import pbandk.wkt.BoolValue

class BooleanConverter : ProtobufConverter<Boolean> {
    override fun convertToProtobuf(obj: Boolean): BoolValue {
        return BoolValue(obj)
    }

    override fun convertFromProtobuf(obj: Any): Boolean {
        return obj.unpack(BoolValue.Companion).value
    }
}
