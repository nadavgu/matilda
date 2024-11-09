package org.matilda.commands.protobuf

import androidx.room.compiler.codegen.XClassName

class CompoundTypeLocator(private val mLocators: List<ProtobufTypeLocator>) : ProtobufTypeLocator {
    override fun locate(className: XClassName) = mLocators.firstNotNullOfOrNull { it.locate(className) }
}