package org.matilda.commands.types

import com.squareup.javapoet.TypeName
import org.matilda.commands.python.PythonTypeName
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
        TODO("Not yet implemented")
    }

    override fun isSupported(type: TypeMirror) =  type.isScalarType()
    override val supportedTypesDescription: String
        get() = "scalar types"
}