package org.matilda.commands.types

import com.google.protobuf.Message
import com.squareup.javapoet.ClassName
import com.squareup.javapoet.TypeName
import org.matilda.commands.python.PythonClassName
import org.matilda.commands.types.TypeConverter.Companion.MAIN_CONVERTERS_PACKAGE
import javax.inject.Inject
import javax.lang.model.type.TypeMirror

class MessageTypeConverter @Inject constructor() : TypeConverter {
    @Inject
    lateinit var mTypes: TypeUtilities

    @Inject
    lateinit var mProtobufTypeTranslator: ProtobufTypeTranslator

    override fun javaConverter(type: TypeMirror, outerConverter: TypeConverter): Pair<String, List<Any>> {
        return Pair("new \$T<>(\$T.class)", listOf(MessageConverter::class.java, type))
    }

    override fun pythonConverter(type: TypeMirror, outerConverter: TypeConverter) =
        Pair("${CONVERTER_CLASS.name}()", listOf(CONVERTER_CLASS))

    override fun pythonType(type: TypeMirror, outerConverter: TypeConverter) = mProtobufTypeTranslator.toPythonType(TypeName.get(type) as ClassName)

    override fun pythonMessageType(type: TypeMirror, outerConverter: TypeConverter) = pythonType(type, outerConverter)

    override fun isSupported(type: TypeMirror, outerConverter: TypeConverter) =  mTypes.isSubtype(type, Message::class.java)
    override val supportedTypesDescription: String
        get() = "protobuf messages"

    companion object {
        private val CONVERTER_CLASS = PythonClassName(MAIN_CONVERTERS_PACKAGE.subpackage("message_converter"),
            "MessageConverter")
    }
}