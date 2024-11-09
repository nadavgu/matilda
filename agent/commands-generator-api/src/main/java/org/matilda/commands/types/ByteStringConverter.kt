package org.matilda.commands.types

import com.google.protobuf.Any
import com.google.protobuf.ByteString
import com.google.protobuf.BytesValue

class ByteStringConverter : ProtobufConverter<ByteString> {
    override fun convertToProtobuf(obj: ByteString): BytesValue {
        return BytesValue.newBuilder().setValue(obj).build()
    }

    override fun convertFromProtobuf(obj: Any): ByteString {
        return obj.unpack(BytesValue::class.java).value
    }
}
