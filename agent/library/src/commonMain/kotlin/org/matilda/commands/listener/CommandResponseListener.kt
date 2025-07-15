package org.matilda.commands.listener

import me.tatarka.inject.annotations.Inject
import org.matilda.commands.protobuf.CommandResponse
import org.matilda.messages.listener.MessageListener
import org.matilda.messages.protobuf.MessageType
import pbandk.decodeFromByteArray

class CommandResponseListener @Inject constructor(private val mMessageListener: MessageListener) {
    fun listen(commandId: Int): CommandResponseListeningInstance {
        val listeningInstance = mMessageListener.listen(MessageType.COMMAND_RESPONSE.value) {
            CommandResponse.decodeFromByteArray(it.data).id == commandId
        }
        return CommandResponseListeningInstance(listeningInstance)
    }
}
