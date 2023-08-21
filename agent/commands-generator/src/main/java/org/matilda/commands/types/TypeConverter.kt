package org.matilda.commands.types

import org.matilda.commands.python.PythonTypeName
import org.matilda.commands.utils.Package
import javax.lang.model.type.TypeMirror

interface TypeConverter {
    fun javaConverter(type: TypeMirror): Pair<String, List<Any>>

    fun pythonConverter(type: TypeMirror): Pair<String, List<PythonTypeName>>

    fun pythonType(type: TypeMirror): PythonTypeName

    fun pythonMessageType(type: TypeMirror): PythonTypeName

    fun isSupported(type: TypeMirror): Boolean

    val supportedTypesDescription: String

    companion object {
        val MAIN_CONVERTERS_PACKAGE = Package("matilda", "commands", "protobuf")
    }
}