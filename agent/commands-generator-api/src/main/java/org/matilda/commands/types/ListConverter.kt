package org.matilda.commands.types

import pbandk.wkt.Any
import org.matilda.commands.protobuf.Some
import pbandk.pack
import pbandk.unpack

class ListConverter<T>(private val mInternalConverter: ProtobufConverter<T>) : ProtobufConverter<List<T>> {
    override fun convertToProtobuf(obj: List<T>): Some {
        return Some(obj.map { element: T ->
            Any.pack(mInternalConverter.convertToProtobuf(element))
        })
    }

    override fun convertFromProtobuf(obj: Any): List<T> {
        val some = obj.unpack(Some.Companion)
        return some.any.map {
            mInternalConverter.convertFromProtobuf(it)
        }
    }
}
