package org.matilda.commands.types

import pbandk.decodeFromByteArray
import pbandk.encodeToByteArray
import com.google.protobuf.Any as GoogleAny
import com.google.protobuf.Message as GoogleMessage
import pbandk.wkt.Any as PbandkAny

class GoogleMessageConverter<T : GoogleMessage>(private val mClass: Class<T>) : ProtobufConverter<T> {
    override fun convertToProtobuf(obj: T): PbandkAny {
        return PbandkAny.decodeFromByteArray(obj.toByteArray())
    }

    override fun convertFromProtobuf(obj: PbandkAny): T {
        return GoogleAny.parseFrom(obj.encodeToByteArray()).unpack(mClass)
    }
}
