package org.matilda.messages.listener

import kotlinx.coroutines.channels.Channel
import org.matilda.messages.Message
import org.matilda.messages.MessageHandlerRegistration
import java.io.Closeable

class MessageListeningInstance(
    private val mMessageChannel: Channel<Message>,
    private val mRegistration: MessageHandlerRegistration
) : Closeable {
    suspend fun waitForMessage(): Message {
        return mMessageChannel.receive()
    }

    fun stop() {
        mRegistration.unregister()
    }

    override fun close() {
        stop()
    }
}
