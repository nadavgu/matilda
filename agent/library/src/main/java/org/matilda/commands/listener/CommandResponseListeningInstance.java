package org.matilda.commands.listener;

import org.matilda.commands.CommandFailedException;
import org.matilda.commands.protobuf.CommandResponse;
import org.matilda.messages.Message;
import org.matilda.messages.listener.MessageListeningInstance;

import java.io.IOException;


public class CommandResponseListeningInstance implements AutoCloseable {
    private final MessageListeningInstance mMessageListeningInstance;

    public CommandResponseListeningInstance(MessageListeningInstance messageListeningInstance) {
        mMessageListeningInstance = messageListeningInstance;
    }

    public byte[] waitForResponse() throws InterruptedException, IOException, CommandFailedException {
        Message message = mMessageListeningInstance.waitForMessage();
        CommandResponse commandResponse = CommandResponse.parseFrom(message.data);
        if (!commandResponse.getSuccess()) {
            throw new CommandFailedException(new String(commandResponse.getResult().toByteArray()));
        }

        return commandResponse.getResult().toByteArray();
    }

    public void stop() {
        mMessageListeningInstance.stop();
    }

    public void close() {
        stop();
    }
}
