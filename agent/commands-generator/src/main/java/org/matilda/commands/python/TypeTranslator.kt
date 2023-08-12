package org.matilda.commands.python

import com.squareup.javapoet.ClassName
import org.matilda.commands.protobuf.ProtobufType
import org.matilda.commands.protobuf.ProtobufTypeLocator
import javax.inject.Inject

class TypeTranslator @Inject constructor() {
    @Inject
    lateinit var mProtobufTypeLocator: ProtobufTypeLocator

    private fun ClassName.toProtobufType() = mProtobufTypeLocator.locate(this) ?:
    throw RuntimeException("Could not find protobuf definition of: $this")

    private fun ProtobufType.toPythonType() =
        PythonTypeName(typePackage.withoutLastPart().subpackage("${typePackage.lastPart}_pb2"), typeName)

    fun toPythonType(className: ClassName) = className.toProtobufType().toPythonType()
}