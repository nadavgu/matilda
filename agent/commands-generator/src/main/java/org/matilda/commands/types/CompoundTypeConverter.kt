package org.matilda.commands.types

import javax.lang.model.type.TypeMirror

class CompoundTypeConverter(private val converters: List<TypeConverter>) : TypeConverter {

    override fun javaConverter(type: TypeMirror, outerConverter: TypeConverter) =
        converters.first { it.isSupported(type, outerConverter) }.javaConverter(type, outerConverter)

    override fun pythonConverter(type: TypeMirror, outerConverter: TypeConverter) =
        converters.first { it.isSupported(type, outerConverter) }.pythonConverter(type, outerConverter)

    override fun pythonType(type: TypeMirror, outerConverter: TypeConverter) =
        converters.first { it.isSupported(type, outerConverter) }.pythonType(type, outerConverter)

    override fun isSupported(type: TypeMirror, outerConverter: TypeConverter) =
        converters.any { it.isSupported(type, outerConverter) }

    override val supportedTypesDescription: String
        get() =
            "one of the following: " + converters.joinToString(", ") {
                it.supportedTypesDescription
            }
}