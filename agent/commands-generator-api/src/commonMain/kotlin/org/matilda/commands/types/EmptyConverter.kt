package org.matilda.commands.types

import pbandk.wkt.Empty
import kotlin.Any
import pbandk.wkt.Any as PbandkAny

class EmptyConverter : ProtobufConverter<Any?> {
    override fun convertToProtobuf(obj: Any?):  Empty {
        return Empty()
    }

    override fun convertFromProtobuf(obj: PbandkAny): Any? {
        return null
    }
}
