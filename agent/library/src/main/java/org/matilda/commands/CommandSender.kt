package org.matilda.commands

import com.google.protobuf.ByteString
import me.tatarka.inject.annotations.Inject
import org.matilda.commands.protobuf.CommandRequest
import org.matilda.messages.Message
import org.matilda.messages.MessageSender
import org.matilda.messages.protobuf.MessageType

class CommandSender @Inject constructor(private var mMessageSender: MessageSender) {
    fun send(commandRegistryId: Int, commandType: Int, commandId: Int, parameter: ByteArray) {
        val commandRequest = CommandRequest.newBuilder()
            .setRegistryId(commandRegistryId)
            .setType(commandType)
            .setId(commandId)
            .setParam(ByteString.copyFrom(parameter))
            .build()
        mMessageSender.send(Message(MessageType.COMMAND.number, commandRequest.toByteArray()))
    }
}
