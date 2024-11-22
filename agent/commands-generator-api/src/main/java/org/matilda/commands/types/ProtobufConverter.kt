package org.matilda.commands.types

import pbandk.Message
import pbandk.wkt.Any

interface ProtobufConverter<T> {
    fun convertToProtobuf(obj: T): Message
    fun convertFromProtobuf(obj: Any): T
}
