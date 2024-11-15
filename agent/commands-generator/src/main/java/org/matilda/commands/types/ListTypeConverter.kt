package org.matilda.commands.types

import androidx.room.compiler.codegen.asClassName
import androidx.room.compiler.codegen.asMutableClassName
import androidx.room.compiler.processing.XType
import org.matilda.commands.python.PythonClassName
import org.matilda.commands.python.pythonListType
import javax.inject.Inject

class ListTypeConverter @Inject constructor() : TypeConverter {
    override fun javaConverter(type: XType, outerConverter: TypeConverter): JavaTypeConverterInfo {
        val (innerFormat, innerArguments) = outerConverter.javaConverter(type.typeArgument, outerConverter)
        return JavaTypeConverterInfo("new \$T<>($innerFormat)",
            listOf(ListConverter::class.java, *innerArguments.toTypedArray()))
    }

    override fun kotlinConverter(type: XType, outerConverter: TypeConverter): JavaTypeConverterInfo {
        val (innerFormat, innerArguments) = outerConverter.kotlinConverter(type.typeArgument, outerConverter)
        return JavaTypeConverterInfo("%T($innerFormat)",
            listOf(ListConverter::class, *innerArguments.toTypedArray()))
    }

    override fun pythonConverter(type: XType, outerConverter: TypeConverter): PythonTypeConverterInfo {
        val (innerConverter, innerRequiredTypes) = outerConverter.pythonConverter(type.typeArgument, outerConverter)
        return PythonTypeConverterInfo("${CONVERTER_CLASS.name}($innerConverter)",
            listOf(CONVERTER_CLASS) + innerRequiredTypes)
    }

    override fun pythonType(type: XType, outerConverter: TypeConverter) =
        pythonListType(outerConverter.pythonType(type.typeArgument, outerConverter))

    override fun isSupported(type: XType, outerConverter: TypeConverter): Boolean {
        if (type.asTypeName().rawTypeName in listOf(List::class.asClassName(), List::class.asMutableClassName())
            && type.typeArguments.size == 1) {
            return outerConverter.isSupported(type.typeArgument, outerConverter)
        }

        return false
    }

    private val XType.typeArgument
        get() = typeArguments[0]

    override val supportedTypesDescription = "lists of other supported types"

    companion object {
        private val CONVERTER_CLASS = PythonClassName(
            TypeConverter.MAIN_CONVERTERS_PACKAGE.subpackage("list_converter"),
            "ListConverter")
    }
}