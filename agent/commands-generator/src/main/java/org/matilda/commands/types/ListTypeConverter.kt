package org.matilda.commands.types

import com.squareup.javapoet.ClassName
import com.squareup.javapoet.ParameterizedTypeName
import com.squareup.javapoet.TypeName
import org.matilda.commands.python.PythonClassName
import org.matilda.commands.python.PythonTypeName
import org.matilda.commands.python.pythonListType
import javax.inject.Inject
import javax.lang.model.type.DeclaredType
import javax.lang.model.type.TypeMirror

class ListTypeConverter @Inject constructor() : TypeConverter {
    override fun javaConverter(type: TypeMirror, outerConverter: TypeConverter): Pair<String, List<Any>> {
        val (innerFormat, innerArguments) = outerConverter.javaConverter(type.typeArgument, outerConverter)
        return Pair("new \$T<>($innerFormat)", listOf(ListConverter::class.java, *innerArguments.toTypedArray()))
    }

    override fun pythonConverter(type: TypeMirror, outerConverter: TypeConverter): Pair<String, List<PythonTypeName>> {
        val (innerConverter, innerRequiredTypes) = outerConverter.pythonConverter(type.typeArgument, outerConverter)
        return Pair("${CONVERTER_CLASS.name}($innerConverter)", listOf(CONVERTER_CLASS) + innerRequiredTypes)
    }

    override fun pythonType(type: TypeMirror, outerConverter: TypeConverter) =
        pythonListType(outerConverter.pythonType(type.typeArgument, outerConverter))

    override fun isSupported(type: TypeMirror, outerConverter: TypeConverter): Boolean {
        val typeName = TypeName.get(type)
        if (type is DeclaredType && typeName is ParameterizedTypeName) {
            if (typeName.rawType.equals(ClassName.get(List::class.java)) && type.typeArguments.size == 1) {
                return outerConverter.isSupported(type.typeArgument, outerConverter)
            }
        }

        return false
    }

    private val TypeMirror.typeArgument
        get() = (this as DeclaredType).typeArguments[0]

    override val supportedTypesDescription = "lists of other supported types"

    companion object {
        private val CONVERTER_CLASS = PythonClassName(
            TypeConverter.MAIN_CONVERTERS_PACKAGE.subpackage("list_converter"),
            "ListConverter")
    }
}