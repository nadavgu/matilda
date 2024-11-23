package org.matilda.commands.types

import pbandk.wkt.Any
import pbandk.wkt.Empty

class EmptyConverter : ProtobufConverter<Void?> {
    override fun convertToProtobuf(obj: Void?):  Empty {
        return Empty()
    }

    override fun convertFromProtobuf(obj: Any): Void? {
        return null
    }
}
