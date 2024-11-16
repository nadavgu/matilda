package org.matilda.messages

import me.tatarka.inject.annotations.Inject
import java.io.DataOutputStream
import java.io.OutputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder

@Inject
class BinaryMessageSender(outputStream: OutputStream, private val mSerializer: MessageSerializer) : MessageSender {
    private val mOutputStream = DataOutputStream(outputStream)

    override fun send(message: Message) {
        val serializedMessage = mSerializer.serialize(message)
        sendLength(serializedMessage.size)
        sendData(serializedMessage)
    }

    private fun sendLength(length: Int) {
        val array = ByteBuffer.allocate(Integer.BYTES)
            .order(ByteOrder.LITTLE_ENDIAN)
            .putInt(length)
            .array()
        mOutputStream.write(array)
    }

    private fun sendData(bytes: ByteArray) {
        mOutputStream.write(bytes)
    }
}
