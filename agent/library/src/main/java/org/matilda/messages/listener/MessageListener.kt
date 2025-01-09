package org.matilda.messages.listener

import kotlinx.coroutines.channels.Channel
import me.tatarka.inject.annotations.Inject
import org.matilda.messages.Message
import org.matilda.messages.MessageHandlerRegistry
import java.util.function.Predicate

class MessageListener @Inject internal constructor(private val mMessageHandlerRegistry: MessageHandlerRegistry) {
    fun listen(messageType: Int, predicate: Predicate<Message>? = null): MessageListeningInstance {
        val channel = Channel<Message>()
        val registration = mMessageHandlerRegistry.registerHandler(messageType) { message: Message ->
            if (predicate == null || predicate.test(message)) {
                channel.send(message)
            }
        }
        return MessageListeningInstance(channel, registration)
    }
}
