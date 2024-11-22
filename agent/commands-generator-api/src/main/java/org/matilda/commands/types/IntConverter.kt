package org.matilda.commands.types

import pbandk.unpack
import pbandk.wkt.Any
import pbandk.wkt.Int32Value

class IntConverter : ProtobufConverter<Int> {
    override fun convertToProtobuf(obj: Int): Int32Value {
        return Int32Value(obj)
    }

    override fun convertFromProtobuf(obj: Any): Int {
        return obj.unpack(Int32Value.Companion).value
    }
}
