package org.matilda.commands.listener

import org.matilda.commands.protobuf.CommandResponse
import org.matilda.messages.listener.MessageListener
import org.matilda.messages.protobuf.MessageType
import javax.inject.Inject

class CommandResponseListener @Inject constructor() {
    @Inject
    lateinit var mMessageListener: MessageListener
    fun listen(commandId: Int): CommandResponseListeningInstance {
        val listeningInstance = mMessageListener.listen(MessageType.COMMAND_RESPONSE_VALUE) {
            CommandResponse.parseFrom(it.data).id == commandId
        }
        return CommandResponseListeningInstance(listeningInstance)
    }
}
