package org.matilda.commands.types

import com.google.protobuf.Message
import com.squareup.javapoet.ClassName
import com.squareup.javapoet.TypeName
import org.matilda.commands.python.PythonTypeName
import javax.inject.Inject
import javax.lang.model.type.TypeMirror

class MessageTypeConverter @Inject constructor() : TypeConverter {
    @Inject
    lateinit var mTypes: TypeUtilities

    @Inject
    lateinit var mProtobufTypeTranslator: ProtobufTypeTranslator

    override fun javaConverter(type: TypeMirror): Pair<String, List<Any>> {
        return Pair("new \$T<>(\$T.class)", listOf(MessageConverter::class.java, type))
    }

    override fun pythonConverter(type: TypeMirror): Pair<String, List<PythonTypeName>> {
        TODO("Not yet implemented")
    }

    override fun pythonType(type: TypeMirror) = mProtobufTypeTranslator.toPythonType(TypeName.get(type) as ClassName)

    override fun isSupported(type: TypeMirror) =  mTypes.isSubtype(type, Message::class.java)
    override val supportedTypesDescription: String
        get() = "protobuf messages"
}