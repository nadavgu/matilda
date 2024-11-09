package org.matilda.messages

import java.io.DataOutputStream
import java.io.OutputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import javax.inject.Inject

class BinaryMessageSender @Inject constructor(outputStream: OutputStream) : MessageSender {
    private val mOutputStream = DataOutputStream(outputStream)

    @Inject
    lateinit var mSerializer: MessageSerializer

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
