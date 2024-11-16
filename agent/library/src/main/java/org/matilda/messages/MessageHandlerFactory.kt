package org.matilda.messages

import me.tatarka.inject.annotations.Inject
import org.matilda.commands.CommandMessageHandler
import org.matilda.messages.handlers.MessageDispatcher
import org.matilda.messages.handlers.MessageHandler
import org.matilda.messages.protobuf.MessageType
import java.util.concurrent.ExecutorService

@Inject
class MessageHandlerFactory(private val mExecutorService: ExecutorService,
                            private val mMessageHandlerRegistry: MessageHandlerRegistry,
                            private val mCommandMessageHandler: CommandMessageHandler) {

    fun create(): MessageHandler {
        mMessageHandlerRegistry.registerHandler(MessageType.COMMAND.number, mCommandMessageHandler)
        return MessageDispatcher(mExecutorService, mMessageHandlerRegistry)
    }
}
