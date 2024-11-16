package org.matilda.messages.listener

import me.tatarka.inject.annotations.Inject
import org.matilda.messages.Message
import org.matilda.messages.MessageHandlerRegistry
import java.util.concurrent.LinkedBlockingQueue
import java.util.function.Predicate

class MessageListener @Inject internal constructor(private val mMessageHandlerRegistry: MessageHandlerRegistry) {
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
