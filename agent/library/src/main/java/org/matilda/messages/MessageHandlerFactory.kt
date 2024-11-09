package org.matilda.messages

import org.matilda.commands.CommandMessageHandler
import org.matilda.messages.handlers.MessageDispatcher
import org.matilda.messages.handlers.MessageHandler
import org.matilda.messages.protobuf.MessageType
import java.util.concurrent.ExecutorService
import javax.inject.Inject

class MessageHandlerFactory @Inject constructor() {
    @Inject
    lateinit var mExecutorService: ExecutorService

    @Inject
    lateinit var mMessageHandlerRegistry: MessageHandlerRegistry

    @Inject
    lateinit var mCommandMessageHandler: CommandMessageHandler
    fun create(): MessageHandler {
        mMessageHandlerRegistry.registerHandler(MessageType.COMMAND.number, mCommandMessageHandler)
        return MessageDispatcher(mExecutorService, mMessageHandlerRegistry)
    }
}
