package org.matilda.commands.types

import pbandk.ByteArr
import pbandk.wkt.Any
import pbandk.wkt.BytesValue

class ByteArrayConverter : ProtobufConverter<ByteArray> {
    private val mByteArrConverter = ByteArrConverter()

    override fun convertToProtobuf(obj: ByteArray): BytesValue {
        return mByteArrConverter.convertToProtobuf(ByteArr(obj))
    }

    override fun convertFromProtobuf(obj: Any): ByteArray {
        return mByteArrConverter.convertFromProtobuf(obj).array
    }
}
