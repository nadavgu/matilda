package org.matilda.commands;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.HashMap;
import java.util.Map;

@Singleton
public class CommandRepository {
    private static final int DEFAULT_PROVIDER_ID = 0;
    private final Map<Integer, CommandRegistry> mCommandsRegistries;
    private int mLastRegistryId;

    @Inject
    public CommandRepository() {
        mCommandsRegistries = new HashMap<>();
        mLastRegistryId = DEFAULT_PROVIDER_ID;
    }

    public int addCommandRegistry(CommandRegistry commandRegistry) {
        mLastRegistryId++;
        mCommandsRegistries.put(mLastRegistryId, commandRegistry);
        return mLastRegistryId;
    }

    public void setDefaultCommandRegistry(CommandRegistry commandRegistry) {
        mCommandsRegistries.put(DEFAULT_PROVIDER_ID, commandRegistry);
    }

    public CommandRegistry getCommandRegistry(int registryId) {
        if (!mCommandsRegistries.containsKey(registryId)) {
            throw new IllegalArgumentException(String.format("Command Provider %d not found", registryId));
        }
        return mCommandsRegistries.get(registryId);
    }

    public Command getCommand(int registryId, int commandType) {
        return getCommandRegistry(registryId).get(commandType);
    }
}
