package org.matilda.messages

import kotlinx.coroutines.CoroutineScope
import me.tatarka.inject.annotations.Inject
import org.matilda.commands.CommandMessageHandler
import org.matilda.messages.handlers.MessageDispatcher
import org.matilda.messages.handlers.MessageHandler
import org.matilda.messages.protobuf.MessageType

@Inject
class MessageHandlerFactory(private val mCoroutineScope: CoroutineScope,
                            private val mMessageHandlerRegistry: MessageHandlerRegistry,
                            private val mCommandMessageHandler: CommandMessageHandler) {

    fun create(): MessageHandler {
        mMessageHandlerRegistry.registerHandler(MessageType.COMMAND.value, mCommandMessageHandler)
        return MessageDispatcher(mCoroutineScope, mMessageHandlerRegistry)
    }
}
