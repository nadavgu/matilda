package org.matilda.commands.types

import com.google.protobuf.ByteString
import pbandk.ByteArr
import pbandk.unpack
import com.google.protobuf.Any as GoogleAny
import com.google.protobuf.Message as GoogleMessage
import pbandk.wkt.Any as PbandkAny

class GoogleMessageConverter<T : GoogleMessage>(private val mClass: Class<T>) : ProtobufConverter<T> {
    override fun convertToProtobuf(obj: T): PbandkAny {
        return GoogleAny.pack(obj).toPbandk()
    }

    override fun convertFromProtobuf(obj: PbandkAny): T {
        return obj.unpack(PbandkAny).toGoogle().unpack(mClass)
    }

    private fun GoogleAny.toPbandk() = PbandkAny(typeUrl = typeUrl, value = ByteArr(value.toByteArray()))

    private fun PbandkAny.toGoogle() = GoogleAny.newBuilder()
        .setTypeUrl(typeUrl)
        .setValue(ByteString.copyFrom(value.array))
        .build()
}