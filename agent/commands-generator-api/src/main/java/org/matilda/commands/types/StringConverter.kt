package org.matilda.commands.types

import pbandk.unpack
import pbandk.wkt.Any
import pbandk.wkt.StringValue

class StringConverter : ProtobufConverter<String> {
    override fun convertToProtobuf(obj: String): StringValue {
        return StringValue(obj)
    }

    override fun convertFromProtobuf(obj: Any): String {
        return obj.unpack(StringValue.Companion).value
    }
}
