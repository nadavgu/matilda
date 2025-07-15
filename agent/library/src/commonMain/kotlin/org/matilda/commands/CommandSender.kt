package org.matilda.commands

import me.tatarka.inject.annotations.Inject
import org.matilda.commands.protobuf.CommandRequest
import org.matilda.messages.Message
import org.matilda.messages.MessageSender
import org.matilda.messages.protobuf.MessageType
import pbandk.ByteArr

class CommandSender @Inject constructor(private var mMessageSender: MessageSender) {
    fun send(commandRegistryId: Int, commandType: Int, commandId: Int, parameter: ByteArray) {
        val commandRequest = CommandRequest(commandRegistryId, commandType, commandId, ByteArr(parameter))
        mMessageSender.send(Message(MessageType.COMMAND.value, commandRequest.encodeToByteArray()))
    }
}
