package org.matilda.commands.types

import com.squareup.javapoet.TypeName
import org.matilda.commands.python.PythonClassName
import org.matilda.commands.python.PythonTypeName
import org.matilda.commands.utils.Package
import java.lang.reflect.Type
import javax.inject.Inject
import javax.lang.model.type.TypeMirror

class ScalarTypeConverter @Inject constructor() : TypeConverter {
    @Inject
    lateinit var mTypes: TypeUtilities

    override fun javaConverter(type: TypeMirror): Pair<String, List<Type>> {
        return Pair("new \$T()", listOf(SCALAR_TYPE_MAP[TypeName.get(type)]!!.javaConverterType))
    }

    override fun pythonConverter(type: TypeMirror): Pair<String, List<PythonTypeName>> {
        val wrapperPythonType = pythonMessageType(type)
        return Pair("${CONVERTER_CLASS.name}(${wrapperPythonType.name})", listOf(CONVERTER_CLASS, wrapperPythonType))
    }

    override fun pythonType(type: TypeMirror) = SCALAR_TYPE_MAP[TypeName.get(type)]!!.pythonType

    override fun pythonMessageType(type: TypeMirror): PythonTypeName {
        val wrapperTypeName = SCALAR_TYPE_MAP[TypeName.get(type)]!!.protobufWrapperJavaType.simpleName
        return PythonClassName(WRAPPERS_PACKAGE, wrapperTypeName)
    }

    override fun isSupported(type: TypeMirror) =  TypeName.get(type) in SCALAR_TYPE_MAP
    override val supportedTypesDescription: String
        get() = "scalar types"


    companion object {
        private val CONVERTER_CLASS = PythonClassName(
            TypeConverter.MAIN_CONVERTERS_PACKAGE.subpackage("scalar_converter"),
            "ScalarConverter")
        private val WRAPPERS_PACKAGE = Package("google", "protobuf", "wrappers_pb2")
    }
}