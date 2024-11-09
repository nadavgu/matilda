package org.matilda.commands.types

import androidx.room.compiler.processing.XType

class CompoundTypeConverter(private val converters: List<TypeConverter>) : TypeConverter {

    override fun javaConverter(type: XType, outerConverter: TypeConverter) =
        converters.first { it.isSupported(type, outerConverter) }.javaConverter(type, outerConverter)

    override fun pythonConverter(type: XType, outerConverter: TypeConverter) =
        converters.first { it.isSupported(type, outerConverter) }.pythonConverter(type, outerConverter)

    override fun pythonType(type: XType, outerConverter: TypeConverter) =
        converters.first { it.isSupported(type, outerConverter) }.pythonType(type, outerConverter)

    override fun isSupported(type: XType, outerConverter: TypeConverter) =
        converters.any { it.isSupported(type, outerConverter) }

    override val supportedTypesDescription: String
        get() =
            "one of the following: " + converters.joinToString(", ") {
                it.supportedTypesDescription
            }
}