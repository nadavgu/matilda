package org.matilda.commands.listener;


import com.google.protobuf.InvalidProtocolBufferException;
import org.matilda.commands.protobuf.CommandResponse;
import org.matilda.messages.listener.MessageListener;
import org.matilda.messages.listener.MessageListeningInstance;
import org.matilda.messages.protobuf.MessageType;

import javax.inject.Inject;

public class CommandResponseListener {
    @Inject
    MessageListener mMessageListener;

    @Inject
    public CommandResponseListener() {}

    public CommandResponseListeningInstance listen(int commandId) {
        MessageListeningInstance listeningInstance = mMessageListener.listen(MessageType.COMMAND_RESPONSE_VALUE, message -> {
            try {
                return CommandResponse.parseFrom(message.data).getId() == commandId;
            } catch (InvalidProtocolBufferException e) {
                throw new RuntimeException(e);
            }
        });

        return new CommandResponseListeningInstance(listeningInstance);
    }
}
