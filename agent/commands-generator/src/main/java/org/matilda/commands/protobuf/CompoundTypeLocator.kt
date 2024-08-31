package org.matilda.commands.protobuf

import com.squareup.javapoet.ClassName

class CompoundTypeLocator(private val mLocators: List<ProtobufTypeLocator>) : ProtobufTypeLocator {
    override fun locate(className: ClassName) = mLocators.firstNotNullOfOrNull { it.locate(className) }
}