package org.matilda.messages

import me.tatarka.inject.annotations.Inject
import org.matilda.messages.protobuf.ProtobufMessage
import pbandk.ByteArr
import pbandk.decodeFromByteArray
import pbandk.encodeToByteArray

@Inject
class ProtobufMessageSerializer : MessageSerializer {
    override fun serialize(message: Message): ByteArray {
        val protobufMessage = ProtobufMessage(message.type, ByteArr(message.data))
        return protobufMessage.encodeToByteArray()
    }

    override fun deserialize(data: ByteArray): Message {
        val protobufMessage = ProtobufMessage.decodeFromByteArray(data)
        return Message(protobufMessage.type, protobufMessage.data.array)
    }
}
