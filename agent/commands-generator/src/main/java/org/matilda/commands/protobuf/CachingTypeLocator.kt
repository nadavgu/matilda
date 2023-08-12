package org.matilda.commands.protobuf

import com.squareup.javapoet.ClassName

class CachingTypeLocator(private val mInternalLocator: ProtobufTypeLocator) : ProtobufTypeLocator {
    private val mCache = mutableMapOf<ClassName, ProtobufType?>()
    override fun locate(className: ClassName): ProtobufType? {
        return mCache.getOrPut(className) {
            mInternalLocator.locate(className)
        }
    }
}