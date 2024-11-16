package org.matilda.commands

import com.google.protobuf.ByteString
import me.tatarka.inject.annotations.Inject
import org.matilda.commands.protobuf.CommandRequest
import org.matilda.commands.protobuf.CommandResponse
import org.matilda.logger.Logger
import org.matilda.messages.Message
import org.matilda.messages.MessageSender
import org.matilda.messages.handlers.MessageHandler
import org.matilda.messages.protobuf.MessageType
import java.io.IOException
import java.io.PrintWriter
import java.io.StringWriter

@Inject
class CommandMessageHandler(private val mMessageSender: MessageSender,
                            @InitializedCommandRepository private val mCommandRepository: CommandRepository,
                            private val mLogger: Logger) : MessageHandler {
    override fun handle(message: Message) {
        try {
            val request = CommandRequest.parseFrom(message.data)
            val result: ByteArray = try {
                mCommandRepository.getCommand(request.registryId, request.type)
                    .run(request.param.toByteArray())
            } catch (e: Throwable) {
                mLogger.log("Failure", e)
                reportCommandFailure(request, e)
                return
            }
            val commandResponse = CommandResponse.newBuilder()
                .setId(request.id)
                .setSuccess(true)
                .setResult(ByteString.copyFrom(result))
                .build()
            mMessageSender.send(Message(MessageType.COMMAND_RESPONSE.number, commandResponse.toByteArray()))
        } catch (e: IOException) {
            mLogger.log("Failed to handle command message", e)
        }
    }

    private fun reportCommandFailure(request: CommandRequest, throwable: Throwable) {
        val commandResponse = CommandResponse.newBuilder()
            .setId(request.id)
            .setSuccess(false)
            .setResult(ByteString.copyFrom(getStackTraceString(throwable).toByteArray()))
            .build()
        mMessageSender.send(Message(MessageType.COMMAND_RESPONSE.number, commandResponse.toByteArray()))
    }

    private fun getStackTraceString(throwable: Throwable): String {
        StringWriter().use { stringWriter ->
            PrintWriter(stringWriter).use { printWriter ->
                throwable.printStackTrace(printWriter)
                return stringWriter.toString()
            }
        }
    }
}
