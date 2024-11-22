package org.matilda.commands.types

import pbandk.wkt.Any
import pbandk.wkt.BytesValue
import pbandk.ByteArr
import pbandk.unpack

class ByteArrConverter : ProtobufConverter<ByteArr> {
    override fun convertToProtobuf(obj: ByteArr): BytesValue {
        return BytesValue(obj)
    }

    override fun convertFromProtobuf(obj: Any): ByteArr {
        return obj.unpack(BytesValue.Companion).value
    }
}
