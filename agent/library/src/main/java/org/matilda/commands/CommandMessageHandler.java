package org.matilda.commands;

import com.google.protobuf.ByteString;
import org.matilda.commands.protobuf.CommandRequest;
import org.matilda.commands.protobuf.CommandResponse;
import org.matilda.logger.Logger;
import org.matilda.messages.Message;
import org.matilda.messages.MessageSender;
import org.matilda.messages.handlers.MessageHandler;

import javax.inject.Inject;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import static org.matilda.messages.protobuf.MessageType.COMMAND_RESPONSE;

public class CommandMessageHandler implements MessageHandler {
    @Inject
    MessageSender mMessageSender;

    @Inject
    CommandRegistry mCommandRegistry;

    @Inject
    Logger mLogger;

    @Inject
    CommandMessageHandler() {}

    @Override
    public void handle(Message message) {
        try {
            CommandRequest request = CommandRequest.parseFrom(message.data);
            byte[] result;
            try {
                result = mCommandRegistry.get(request.getType()).run(request.getParam().toByteArray());
            } catch (Throwable e) {
                mLogger.log("Failure", e);
                reportCommandFailure(request, e);
                return;
            }
            CommandResponse commandResponse = CommandResponse.newBuilder()
                    .setId(request.getId())
                    .setSuccess(true)
                    .setResult(ByteString.copyFrom(result))
                    .build();
            mMessageSender.send(new Message(COMMAND_RESPONSE.getNumber(), commandResponse.toByteArray()));
        } catch (IOException e) {
            mLogger.log("Failed to handle command message", e);
        }
    }

    private void reportCommandFailure(CommandRequest request, Throwable throwable) throws IOException {
        CommandResponse commandResponse = CommandResponse.newBuilder()
                .setId(request.getId())
                .setSuccess(false)
                .setResult(ByteString.copyFrom(getStackTraceString(throwable).getBytes()))
                .build();
        mMessageSender.send(new Message(COMMAND_RESPONSE.getNumber(), commandResponse.toByteArray()));
    }

    private String getStackTraceString(Throwable throwable) throws IOException {
        try (StringWriter stringWriter = new StringWriter(); PrintWriter printWriter = new PrintWriter(stringWriter)) {
            throwable.printStackTrace(printWriter);
            return stringWriter.toString();
        }
    }
}
