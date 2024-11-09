package org.matilda.commands.types

import com.google.protobuf.Any
import org.matilda.commands.protobuf.Some

class ListConverter<T>(private val mInternalConverter: ProtobufConverter<T>) : ProtobufConverter<List<T>> {
    override fun convertToProtobuf(obj: List<T>): Some {
        val builder = Some.newBuilder()
        obj.forEach { element: T ->
            builder.addAny(Any.pack(mInternalConverter.convertToProtobuf(element)))
        }
        return builder.build()
    }

    override fun convertFromProtobuf(obj: Any): List<T> {
        val some = obj.unpack(Some::class.java)
        return some.anyList.map {
            mInternalConverter.convertFromProtobuf(it)
        }
    }
}
