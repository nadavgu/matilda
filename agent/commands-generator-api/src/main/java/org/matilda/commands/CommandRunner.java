package org.matilda.commands;

public interface CommandRunner {
    byte[] run(int registryId, int commandType, byte[] parameter);
}
