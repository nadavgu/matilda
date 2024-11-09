package org.matilda.messages.listener

import org.matilda.messages.Message
import org.matilda.messages.MessageHandlerRegistry
import java.util.concurrent.LinkedBlockingQueue
import java.util.function.Predicate
import javax.inject.Inject

class MessageListener @Inject internal constructor() {
    @Inject
    lateinit var mMessageHandlerRegistry: MessageHandlerRegistry
    fun listen(messageType: Int, predicate: Predicate<Message>? = null): MessageListeningInstance {
        val queue = LinkedBlockingQueue<Message>()
        val registration = mMessageHandlerRegistry.registerHandler(messageType) { message: Message ->
            if (predicate == null || predicate.test(message)) {
                queue.add(message)
            }
        }
        return MessageListeningInstance(queue, registration)
    }
}
