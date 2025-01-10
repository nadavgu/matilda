package org.matilda.messages.listener

import kotlinx.coroutines.channels.Channel
import org.matilda.messages.Message
import org.matilda.messages.MessageHandlerRegistration

class MessageListeningInstance(
    private val mMessageChannel: Channel<Message>,
    private val mRegistration: MessageHandlerRegistration
) {
    suspend fun waitForMessage(): Message {
        return mMessageChannel.receive()
    }

    fun stop() {
        mRegistration.unregister()
    }
}
