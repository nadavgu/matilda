package org.matilda.commands.types

import org.matilda.commands.python.PythonClassName
import org.matilda.commands.python.PythonTypeName
import org.matilda.commands.utils.Package
import javax.inject.Inject
import javax.lang.model.type.TypeMirror

class ScalarTypeConverter @Inject constructor() : TypeConverter {
    override fun javaConverter(type: TypeMirror, outerConverter: TypeConverter): JavaTypeConverterInfo {
        return JavaTypeConverterInfo("new \$T()", listOf(type.scalarJavaConverterType))
    }

    override fun pythonConverter(type: TypeMirror, outerConverter: TypeConverter): Pair<String, List<PythonTypeName>> {
        val wrapperTypeName = type.scalarProtobufWrapperJavaType.simpleName
        val wrapperPythonType = PythonClassName(WRAPPERS_PACKAGE, wrapperTypeName)
        return Pair("${CONVERTER_CLASS.name}(${wrapperPythonType.name})", listOf(CONVERTER_CLASS, wrapperPythonType))
    }

    override fun pythonType(type: TypeMirror, outerConverter: TypeConverter) = type.scalarPythonType

    override fun isSupported(type: TypeMirror, outerConverter: TypeConverter) =  type.isScalar()
    override val supportedTypesDescription: String
        get() = "scalar types"


    companion object {
        private val CONVERTER_CLASS = PythonClassName(
            TypeConverter.MAIN_CONVERTERS_PACKAGE.subpackage("scalar_converter"),
            "ScalarConverter")
        private val WRAPPERS_PACKAGE = Package("google", "protobuf", "wrappers_pb2")
    }
}