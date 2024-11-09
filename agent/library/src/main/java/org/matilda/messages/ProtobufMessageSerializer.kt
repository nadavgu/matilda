package org.matilda.messages

import com.google.protobuf.ByteString
import org.matilda.messages.protobuf.ProtobufMessage
import java.io.InputStream
import javax.inject.Inject

class ProtobufMessageSerializer @Inject constructor() : MessageSerializer {
    @Inject
    lateinit var mInputStream: InputStream
    override fun serialize(message: Message): ByteArray {
        val protobufMessage = ProtobufMessage.newBuilder()
            .setType(message.type)
            .setData(ByteString.copyFrom(message.data))
            .build()
        return protobufMessage.toByteArray()
    }

    override fun deserialize(data: ByteArray): Message {
        val protobufMessage = ProtobufMessage.parseFrom(data)
        return Message(protobufMessage.type, protobufMessage.data.toByteArray())
    }
}
