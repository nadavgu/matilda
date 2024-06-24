package org.matilda.commands;

import com.google.protobuf.ByteString;
import org.matilda.commands.protobuf.CommandRequest;
import org.matilda.messages.Message;
import org.matilda.messages.MessageSender;
import org.matilda.messages.protobuf.MessageType;

import javax.inject.Inject;
import java.io.IOException;

public class CommandSender {
    @Inject
    MessageSender mMessageSender;

    @Inject
    public CommandSender() {}

    void send(int commandRegistryId, int commandType, int commandId, byte[] parameter) throws IOException {
        CommandRequest commandRequest = CommandRequest.newBuilder()
                .setRegistryId(commandRegistryId)
                .setType(commandType)
                .setId(commandId)
                .setParam(ByteString.copyFrom(parameter))
                .build();
        mMessageSender.send(new Message(MessageType.COMMAND.getNumber(), commandRequest.toByteArray()));
    }
}
