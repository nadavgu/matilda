package org.matilda.commands;

import java.io.IOException;


public interface CommandRunner {
    byte[] run(int registryId, int commandType, byte[] parameter) throws IOException, InterruptedException,
            CommandFailedException;
}
