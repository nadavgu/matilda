package org.matilda.commands.types

import com.squareup.javapoet.TypeName
import javax.inject.Inject
import javax.lang.model.type.TypeMirror
import javax.lang.model.util.Types

class BoxedTypeConverter @Inject constructor() : TypeConverter {
    @Inject
    lateinit var mTypes: Types
    override fun javaConverter(type: TypeMirror, outerConverter: TypeConverter) =
        outerConverter.javaConverter(mTypes.unboxedType(type))

    override fun pythonConverter(type: TypeMirror, outerConverter: TypeConverter) =
        outerConverter.pythonConverter(mTypes.unboxedType(type))

    override fun pythonType(type: TypeMirror, outerConverter: TypeConverter) =
        outerConverter.pythonType(mTypes.unboxedType(type))

    override fun isSupported(type: TypeMirror, outerConverter: TypeConverter) =
        TypeName.get(type).isBoxedPrimitive && outerConverter.isSupported(mTypes.unboxedType(type))
    override val supportedTypesDescription: String
        get() = "boxed scalar types"
}