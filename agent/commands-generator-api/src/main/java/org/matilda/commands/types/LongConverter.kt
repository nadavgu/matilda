package org.matilda.commands.types

import pbandk.unpack
import pbandk.wkt.Any
import pbandk.wkt.Int64Value

class LongConverter : ProtobufConverter<Long> {
    override fun convertToProtobuf(obj: Long): Int64Value {
        return Int64Value(obj)
    }

    override fun convertFromProtobuf(obj: Any): Long {
        return obj.unpack(Int64Value.Companion).value
    }
}
