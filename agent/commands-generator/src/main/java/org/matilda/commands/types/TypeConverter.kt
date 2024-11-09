package org.matilda.commands.types

import androidx.room.compiler.processing.XType
import org.matilda.commands.python.PythonTypeName
import org.matilda.commands.utils.Package

interface TypeConverter {
    fun javaConverter(type: XType, outerConverter: TypeConverter): JavaTypeConverterInfo

    fun pythonConverter(type: XType, outerConverter: TypeConverter): PythonTypeConverterInfo

    fun pythonType(type: XType, outerConverter: TypeConverter): PythonTypeName

    fun isSupported(type: XType, outerConverter: TypeConverter): Boolean

    val supportedTypesDescription: String

    companion object {
        val MAIN_CONVERTERS_PACKAGE = Package("matilda", "commands", "protobuf")
    }
}

fun TypeConverter.javaConverter(type: XType) = javaConverter(type, this)
fun TypeConverter.pythonConverter(type: XType) = pythonConverter(type, this)
fun TypeConverter.pythonType(type: XType) = pythonType(type, this)
fun TypeConverter.isSupported(type: XType) = isSupported(type, this)
