package org.matilda.commands

import me.tatarka.inject.annotations.Inject
import org.matilda.commands.protobuf.CommandRequest
import org.matilda.commands.protobuf.CommandResponse
import org.matilda.logger.Logger
import org.matilda.messages.Message
import org.matilda.messages.MessageSender
import org.matilda.messages.handlers.MessageHandler
import org.matilda.messages.protobuf.MessageType
import pbandk.ByteArr
import pbandk.decodeFromByteArray
import pbandk.encodeToByteArray

@Inject
class CommandMessageHandler(private val mMessageSender: MessageSender,
                            @InitializedCommandRepository private val mCommandRepository: CommandRepository,
                            private val mLogger: Logger) : MessageHandler {
    override suspend fun handle(message: Message) {
        val request = CommandRequest.decodeFromByteArray(message.data)
        val result: ByteArray = try {
            mCommandRepository.getCommand(request.registryId, request.type)
                .run(request.param.array)
        } catch (e: Throwable) {
            mLogger.log("Failure", e)
            reportCommandFailure(request, e)
            return
        }
        val commandResponse = CommandResponse(request.id, true, ByteArr(result))
        mMessageSender.send(Message(MessageType.COMMAND_RESPONSE.value, commandResponse.encodeToByteArray()))
    }

    private fun reportCommandFailure(request: CommandRequest, throwable: Throwable) {
        val commandResponse = CommandResponse(request.id, false,
            ByteArr(throwable.stackTraceToString().toByteArray()))
        mMessageSender.send(Message(MessageType.COMMAND_RESPONSE.value, commandResponse.encodeToByteArray()))
    }
}
