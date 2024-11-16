package org.matilda.messages

import me.tatarka.inject.annotations.Inject
import org.matilda.commands.MatildaScope
import org.matilda.messages.handlers.MessageHandler

@MatildaScope
@Inject
class MessageHandlerRegistry : MessageHandler {
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
