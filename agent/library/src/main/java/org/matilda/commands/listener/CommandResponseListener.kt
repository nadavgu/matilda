package org.matilda.commands.listener

import me.tatarka.inject.annotations.Inject
import org.matilda.commands.protobuf.CommandResponse
import org.matilda.messages.listener.MessageListener
import org.matilda.messages.protobuf.MessageType

class CommandResponseListener @Inject constructor(private val mMessageListener: MessageListener) {
    fun listen(commandId: Int): CommandResponseListeningInstance {
        val listeningInstance = mMessageListener.listen(MessageType.COMMAND_RESPONSE_VALUE) {
            CommandResponse.parseFrom(it.data).id == commandId
        }
        return CommandResponseListeningInstance(listeningInstance)
    }
}
