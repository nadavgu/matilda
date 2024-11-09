package org.matilda.commands;

public interface Command {
    byte[] run(byte[] parameter) throws Throwable;
}
