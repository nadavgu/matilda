package org.matilda.messages

import com.google.protobuf.ByteString
import me.tatarka.inject.annotations.Inject
import org.matilda.messages.protobuf.ProtobufMessage

@Inject
class ProtobufMessageSerializer : MessageSerializer {
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
