package org.matilda.messages

import kotlinx.io.Source
import kotlinx.io.readByteArray
import kotlinx.io.readIntLe
import me.tatarka.inject.annotations.Inject

@Inject
class BinaryMessageReceiver(private val mSerializer: MessageSerializer, private val mSource: Source) : MessageReceiver {
    override fun receive(): Message {
        val length = readLength()
        val data = readData(length)
        return mSerializer.deserialize(data)
    }

    private fun readData(length: Int): ByteArray {
        return mSource.readByteArray(length)
    }

    private fun readLength(): Int {
        return mSource.readIntLe()
    }
}
