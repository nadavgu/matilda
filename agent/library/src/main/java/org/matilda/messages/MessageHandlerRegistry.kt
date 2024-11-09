package org.matilda.messages

import org.matilda.messages.handlers.MessageHandler
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MessageHandlerRegistry @Inject constructor() : MessageHandler {
    private val mMessageHandlers = mutableMapOf<Int, MutableList<MessageHandler>>()
    fun registerHandler(type: Int, handler: MessageHandler): MessageHandlerRegistration {
        mMessageHandlers.getOrPut(type) { mutableListOf() }.add(handler)
        return MessageHandlerRegistration(this, type, handler)
    }

    fun unregisterHandler(type: Int, handler: MessageHandler) {
        mMessageHandlers[type]?.remove(handler)
    }

    override fun handle(message: Message) {
        mMessageHandlers[message.type]?.forEach { it.handle(message) }
    }
}
