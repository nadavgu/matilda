package org.matilda.commands;

public class EchoCommand implements Command {
    @Override
    public byte[] run(byte[] parameter) {
        return parameter;
    }
}
