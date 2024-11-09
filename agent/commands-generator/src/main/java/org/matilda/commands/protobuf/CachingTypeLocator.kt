package org.matilda.commands.protobuf

import androidx.room.compiler.codegen.XClassName

class CachingTypeLocator(private val mInternalLocator: ProtobufTypeLocator) : ProtobufTypeLocator {
    private val mCache = mutableMapOf<XClassName, ProtobufType?>()
    override fun locate(className: XClassName): ProtobufType? {
        return mCache.getOrPut(className) {
            mInternalLocator.locate(className)
        }
    }
}