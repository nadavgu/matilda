package org.matilda.messages

interface MessageSerializer {
    fun serialize(message: Message): ByteArray

    fun deserialize(data: ByteArray): Message
}
