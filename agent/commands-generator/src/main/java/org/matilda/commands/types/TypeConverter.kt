package org.matilda.commands.types

import org.matilda.commands.python.PythonTypeName
import org.matilda.commands.utils.Package
import javax.lang.model.type.TypeMirror

interface TypeConverter {
    fun javaConverter(type: TypeMirror, outerConverter: TypeConverter): JavaTypeConverterInfo

    fun pythonConverter(type: TypeMirror, outerConverter: TypeConverter): PythonTypeConverterInfo

    fun pythonType(type: TypeMirror, outerConverter: TypeConverter): PythonTypeName

    fun isSupported(type: TypeMirror, outerConverter: TypeConverter): Boolean

    val supportedTypesDescription: String

    companion object {
        val MAIN_CONVERTERS_PACKAGE = Package("matilda", "commands", "protobuf")
    }
}

fun TypeConverter.javaConverter(type: TypeMirror) = javaConverter(type, this)
fun TypeConverter.pythonConverter(type: TypeMirror) = pythonConverter(type, this)
fun TypeConverter.pythonType(type: TypeMirror) = pythonType(type, this)
fun TypeConverter.isSupported(type: TypeMirror) = isSupported(type, this)
