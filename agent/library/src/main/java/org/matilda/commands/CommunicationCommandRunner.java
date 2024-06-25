package org.matilda.commands;

import org.matilda.commands.listener.CommandResponseListener;
import org.matilda.commands.listener.CommandResponseListeningInstance;

import javax.inject.Inject;
import java.io.IOException;


public class CommunicationCommandRunner implements CommandRunnerInterface {
    @Inject
    CommandSender mCommandSender;

    @Inject
    CommandResponseListener mCommandResponseListener;

    @Inject
    CommandIdGenerator mCommandIdGenerator;

    @Inject
    CommunicationCommandRunner() {}

    public byte[] run(int registryId, int commandType, byte[] parameter) throws IOException, InterruptedException,
            CommandFailedException {
        int commandId = mCommandIdGenerator.generate();
        try (CommandResponseListeningInstance listeningInstance = mCommandResponseListener.listen(commandId)) {
            mCommandSender.send(registryId, commandType, commandId, parameter);
            return listeningInstance.waitForResponse();
        }
    }
}
