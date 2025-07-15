package org.matilda.messages

import kotlinx.io.Sink
import kotlinx.io.writeIntLe
import me.tatarka.inject.annotations.Inject

@Inject
class BinaryMessageSender(private val mSink: Sink, private val mSerializer: MessageSerializer) : MessageSender {
    override fun send(message: Message) {
        val serializedMessage = mSerializer.serialize(message)
        sendLength(serializedMessage.size)
        sendData(serializedMessage)
        mSink.flush()
    }

    private fun sendLength(length: Int) {
        mSink.writeIntLe(length)
    }

    private fun sendData(bytes: ByteArray) {
        mSink.write(bytes)
    }
}
