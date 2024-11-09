package org.matilda.messages.handlers

import org.matilda.messages.Message
import java.util.concurrent.ExecutorService

class MessageDispatcher(private val mExecutorService: ExecutorService, private val mMessageHandler: MessageHandler) :
    MessageHandler {
    override fun handle(message: Message) {
        mExecutorService.submit { mMessageHandler.handle(message) }
    }
}
