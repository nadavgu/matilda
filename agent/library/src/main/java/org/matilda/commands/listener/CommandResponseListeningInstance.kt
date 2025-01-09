package org.matilda.commands.listener

import org.matilda.commands.CommandFailedException
import org.matilda.commands.protobuf.CommandResponse
import org.matilda.messages.listener.MessageListeningInstance
import pbandk.decodeFromByteArray
import java.io.Closeable

class CommandResponseListeningInstance(private val mMessageListeningInstance: MessageListeningInstance) : Closeable {
    suspend fun waitForResponse(): ByteArray {
        val message = mMessageListeningInstance.waitForMessage()
        val commandResponse = CommandResponse.decodeFromByteArray(message.data)
        if (!commandResponse.success) {
            throw CommandFailedException(String(commandResponse.result.array))
        }
        return commandResponse.result.array
    }

    private fun stop() {
        mMessageListeningInstance.stop()
    }

    override fun close() {
        stop()
    }
}
