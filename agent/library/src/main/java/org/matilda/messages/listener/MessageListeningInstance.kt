package org.matilda.messages.listener

import org.matilda.messages.Message
import org.matilda.messages.MessageHandlerRegistration
import java.io.Closeable
import java.util.concurrent.BlockingQueue

class MessageListeningInstance(
    private val mMessageQueue: BlockingQueue<Message>,
    private val mRegistration: MessageHandlerRegistration
) : Closeable {
    fun waitForMessage(): Message {
        return mMessageQueue.take()
    }

    fun stop() {
        mRegistration.unregister()
    }

    override fun close() {
        stop()
    }
}
