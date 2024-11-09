package org.matilda.commands.types

import com.google.protobuf.Any
import com.google.protobuf.ByteString
import com.google.protobuf.BytesValue

class ByteArrayConverter : ProtobufConverter<ByteArray> {
    private val mByteStringConverter = ByteStringConverter()

    override fun convertToProtobuf(obj: ByteArray): BytesValue {
        return mByteStringConverter.convertToProtobuf(ByteString.copyFrom(obj))
    }

    override fun convertFromProtobuf(obj: Any): ByteArray {
        return mByteStringConverter.convertFromProtobuf(obj).toByteArray()
    }
}
