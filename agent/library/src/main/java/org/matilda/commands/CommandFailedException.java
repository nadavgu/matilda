package org.matilda.commands;

public class CommandFailedException extends Exception {
    public CommandFailedException(String message) {
        super(message);
    }
}
