package org.matilda.commands.types

import androidx.room.compiler.processing.XType
import androidx.room.compiler.processing.isVoid
import org.matilda.commands.python.PythonClassName
import org.matilda.commands.python.PythonTypeName
import javax.inject.Inject

class VoidTypeConverter @Inject constructor() : TypeConverter {
    override fun javaConverter(type: XType, outerConverter: TypeConverter): JavaTypeConverterInfo {
        return JavaTypeConverterInfo("new \$T()", listOf(EmptyConverter::class.java))
    }
    override fun kotlinConverter(type: XType, outerConverter: TypeConverter): JavaTypeConverterInfo {
        return JavaTypeConverterInfo("new %T()", listOf(EmptyConverter::class))
    }

    override fun pythonConverter(type: XType, outerConverter: TypeConverter): PythonTypeConverterInfo {
        return PythonTypeConverterInfo("${CONVERTER_CLASS.name}()", listOf(CONVERTER_CLASS))
    }

    override fun pythonType(type: XType, outerConverter: TypeConverter) = PythonTypeName.NONE

    override fun isSupported(type: XType, outerConverter: TypeConverter) =  type.isVoid()
    override val supportedTypesDescription: String
        get() = "void"


    companion object {
        private val CONVERTER_CLASS = PythonClassName(
            TypeConverter.MAIN_CONVERTERS_PACKAGE.subpackage("empty_converter"), "EmptyConverter")
    }
}