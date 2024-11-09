package org.matilda.messages

import java.io.DataInputStream
import java.io.InputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import javax.inject.Inject

class BinaryMessageReceiver @Inject constructor(inputStream: InputStream) : MessageReceiver {
    private val mInputStream = DataInputStream(inputStream)

    @Inject
    lateinit var mSerializer: MessageSerializer


    override fun receive(): Message {
        val length = readLength()
        val data = readData(length)
        return mSerializer.deserialize(data)
    }

    private fun readData(length: Int): ByteArray {
        val bytes = ByteArray(length)
        mInputStream.readFully(bytes)
        return bytes
    }

    private fun readLength(): Int {
        val lengthBytes = ByteArray(Integer.BYTES)
        mInputStream.readFully(lengthBytes)
        return ByteBuffer.wrap(lengthBytes)
            .order(ByteOrder.LITTLE_ENDIAN)
            .getInt()
    }
}
