package org.matilda.commands.types

import com.squareup.javapoet.TypeName
import org.matilda.commands.python.PythonClassName
import org.matilda.commands.python.PythonTypeName
import java.lang.reflect.Type
import javax.inject.Inject
import javax.lang.model.type.TypeMirror

class VoidTypeConverter @Inject constructor() : TypeConverter {
    override fun javaConverter(type: TypeMirror, outerConverter: TypeConverter): Pair<String, List<Type>> {
        return Pair("new \$T()", listOf(EmptyConverter::class.java))
    }

    override fun pythonConverter(type: TypeMirror, outerConverter: TypeConverter): Pair<String, List<PythonTypeName>> {
        return Pair("${CONVERTER_CLASS.name}()", listOf(CONVERTER_CLASS))
    }

    override fun pythonType(type: TypeMirror, outerConverter: TypeConverter) = PythonTypeName.NONE

    override fun isSupported(type: TypeMirror, outerConverter: TypeConverter) =  TypeName.get(type) == TypeName.VOID
    override val supportedTypesDescription: String
        get() = "void"


    companion object {
        private val CONVERTER_CLASS = PythonClassName(
            TypeConverter.MAIN_CONVERTERS_PACKAGE.subpackage("empty_converter"), "EmptyConverter")
    }
}