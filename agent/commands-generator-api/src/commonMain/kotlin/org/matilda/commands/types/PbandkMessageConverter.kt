package org.matilda.commands.types

import pbandk.Message
import pbandk.unpack
import pbandk.wkt.Any

class PbandkMessageConverter<T : Message>(private val mCompanion: Message.Companion<T>) : ProtobufConverter<T> {
    override fun convertToProtobuf(obj: T): T {
        return obj
    }

    override fun convertFromProtobuf(obj: Any): T {
        return obj.unpack(mCompanion)
    }
}
