package org.matilda.commands.types

import com.google.protobuf.Any
import com.google.protobuf.StringValue

class StringConverter : ProtobufConverter<String> {
    override fun convertToProtobuf(obj: String): StringValue {
        return StringValue.newBuilder().setValue(obj).build()
    }

    override fun convertFromProtobuf(obj: Any): String {
        return obj.unpack(StringValue::class.java).value
    }
}
