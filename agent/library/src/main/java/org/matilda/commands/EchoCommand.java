package org.matilda.commands;

@ProtobufCommand
public class EchoCommand implements Command {
    @Override
    public byte[] run(byte[] parameter) {
        return parameter;
    }
}
