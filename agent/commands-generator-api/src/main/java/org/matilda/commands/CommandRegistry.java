package org.matilda.commands;

import java.util.HashMap;
import java.util.Map;

public class CommandRegistry {
    private final Map<Integer, Command> mCommands;

    public CommandRegistry() {
        mCommands = new HashMap<>();
    }

    public void addCommand(int type, Command command) {
        if (mCommands.containsKey(type)) {
            throw new RuntimeException(String.format("Command %d already handled by %s", type, mCommands.get(type)));
        }
        mCommands.put(type, command);
    }

    public Command get(int type) {
        return mCommands.get(type);
    }
}
