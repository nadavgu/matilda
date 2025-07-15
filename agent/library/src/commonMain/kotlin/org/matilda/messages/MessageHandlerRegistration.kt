package org.matilda.messages

import org.matilda.messages.handlers.MessageHandler

class MessageHandlerRegistration(
    private val mMessageHandlerRegistry: MessageHandlerRegistry, private val mMessageType: Int,
    private val mMessageHandler: MessageHandler
) {
    fun unregister() {
        mMessageHandlerRegistry.unregisterHandler(mMessageType, mMessageHandler)
    }
}
