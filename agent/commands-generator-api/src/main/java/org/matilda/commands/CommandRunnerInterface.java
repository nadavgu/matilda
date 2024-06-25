package org.matilda.commands;

import java.io.IOException;


public interface CommandRunnerInterface {
    byte[] run(int registryId, int commandType, byte[] parameter) throws IOException, InterruptedException,
            CommandFailedException;
}
