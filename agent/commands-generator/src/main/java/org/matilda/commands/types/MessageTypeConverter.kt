package org.matilda.commands.types

import com.google.protobuf.Message
import org.matilda.commands.python.writer.PythonClass
import java.lang.reflect.Type
import javax.inject.Inject
import javax.lang.model.type.TypeMirror

class MessageTypeConverter @Inject constructor() : TypeConverter {
    @Inject
    lateinit var mTypes: TypeUtilities

    override fun javaConverter(type: TypeMirror): Pair<String, List<Type>> {
        return Pair("new \$T()", listOf(MessageConverter::class.java))
    }

    override fun pythonConverter(type: TypeMirror): Pair<String, List<PythonClass>> {
        TODO("Not yet implemented")
    }

    override fun isSupported(type: TypeMirror) =  mTypes.isSubtype(type, Message::class.java)
    override val supportedTypesDescription: String
        get() = "protobuf messages"
}