package org.matilda.messages.handlers

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.matilda.messages.Message

class MessageDispatcher(private val mCoroutineScope: CoroutineScope, private val mMessageHandler: MessageHandler) :
    MessageHandler {
    override fun handle(message: Message) {
        mCoroutineScope.launch { mMessageHandler.handle(message) }
    }
}
