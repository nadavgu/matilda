package org.matilda.commands.listener

import org.matilda.commands.CommandFailedException
import org.matilda.commands.protobuf.CommandResponse
import org.matilda.messages.listener.MessageListeningInstance
import java.io.Closeable

class CommandResponseListeningInstance(private val mMessageListeningInstance: MessageListeningInstance) : Closeable {
    @Throws(InterruptedException::class)
    fun waitForResponse(): ByteArray {
        val message = mMessageListeningInstance.waitForMessage()
        val commandResponse = CommandResponse.parseFrom(message.data)
        if (!commandResponse.success) {
            throw CommandFailedException(String(commandResponse.result.toByteArray()))
        }
        return commandResponse.result.toByteArray()
    }

    private fun stop() {
        mMessageListeningInstance.stop()
    }

    override fun close() {
        stop()
    }
}
