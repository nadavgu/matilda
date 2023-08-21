package org.matilda.commands.types

import javax.lang.model.type.TypeMirror

class CompoundTypeConverter(private val converters: List<TypeConverter>) : TypeConverter {

    override fun javaConverter(type: TypeMirror) = converters.first { it.isSupported(type) }.javaConverter(type)

    override fun pythonConverter(type: TypeMirror) = converters.first { it.isSupported(type) }.pythonConverter(type)

    override fun pythonType(type: TypeMirror) = converters.first { it.isSupported(type) }.pythonType(type)

    override fun pythonMessageType(type: TypeMirror) = converters.first { it.isSupported(type) }.pythonMessageType(type)

    override fun isSupported(type: TypeMirror) = converters.any { it.isSupported(type) }

    override val supportedTypesDescription: String
        get() =
            "one of the following:\n" + converters.joinToString("\n") {
                "- ${it.supportedTypesDescription}"
            }
}