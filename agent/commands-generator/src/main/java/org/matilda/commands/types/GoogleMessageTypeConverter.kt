package org.matilda.commands.types

import androidx.room.compiler.processing.XType
import com.google.protobuf.Message
import com.squareup.kotlinpoet.javapoet.KotlinPoetJavaPoetPreview
import com.squareup.kotlinpoet.javapoet.toKTypeName
import org.matilda.commands.python.PythonClassName
import org.matilda.commands.types.TypeConverter.Companion.MAIN_CONVERTERS_PACKAGE
import javax.inject.Inject

@OptIn(KotlinPoetJavaPoetPreview::class)
class GoogleMessageTypeConverter @Inject constructor() : TypeConverter {
    @Inject
    lateinit var mTypes: TypeUtilities

    @Inject
    lateinit var mProtobufTypeTranslator: ProtobufTypeTranslator

    override fun javaConverter(type: XType, outerConverter: TypeConverter): JavaTypeConverterInfo {
        return JavaTypeConverterInfo("new \$T<>(\$T.class)", listOf(GoogleMessageConverter::class.java, type.typeName))
    }

    override fun kotlinConverter(type: XType, outerConverter: TypeConverter): JavaTypeConverterInfo {
        return JavaTypeConverterInfo("%T(%T::class.java)",
            listOf(GoogleMessageConverter::class, type.typeName.toKTypeName()))
    }

    override fun pythonConverter(type: XType, outerConverter: TypeConverter): PythonTypeConverterInfo {
        val pythonType = pythonType(type, outerConverter)
        return PythonTypeConverterInfo("${CONVERTER_CLASS.name}(${pythonType.name})",
            listOf(CONVERTER_CLASS, pythonType))
    }

    override fun pythonType(type: XType, outerConverter: TypeConverter) = mProtobufTypeTranslator.toPythonType(type.typeElement!!.asClassName())

    override fun isSupported(type: XType, outerConverter: TypeConverter) =  mTypes.isSubtype(type, Message::class.java)
    override val supportedTypesDescription: String
        get() = "protobuf messages"

    companion object {
        private val CONVERTER_CLASS = PythonClassName(MAIN_CONVERTERS_PACKAGE.subpackage("message_converter"),
            "MessageConverter")
    }
}