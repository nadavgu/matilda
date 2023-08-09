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

import static org.matilda.messages.protobuf.MessageType.COMMAND_RESPONSE;

public class CommandMessageHandler implements MessageHandler {
    @Inject
    MessageSender mMessageSender;

    @Inject
    CommandRegistry mCommandRegistry;

    @Inject
    Logger mLogger;

    @Override
    public void handle(Message message) {
        try {
            CommandRequest request = CommandRequest.parseFrom(message.data);
            byte[] result = mCommandRegistry.get(request.getType()).run(request.getParam().toByteArray());
            CommandResponse commandResponse = CommandResponse.newBuilder()
                    .setId(request.getId())
                    .setReturnValue(ByteString.copyFrom(result))
                    .build();
            mMessageSender.send(new Message(COMMAND_RESPONSE.getNumber(), commandResponse.toByteArray()));
        } catch (IOException e) {
            mLogger.log("Failed to handle command message", e);
        }
    }
}
