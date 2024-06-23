package org.matilda.commands;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.HashMap;
import java.util.Map;

@Singleton
public class CommandRepository {
    private static final int DEFAULT_PROVIDER_ID = 0;
    private final Map<Integer, CommandRegistry> mCommandsRegistries;
    private int mLastProviderId;

    @Inject
    public CommandRepository(CommandRegistry defaultRegistry) {
        mCommandsRegistries = new HashMap<Integer, CommandRegistry>() {{
            put(DEFAULT_PROVIDER_ID, defaultRegistry);
        }};
        mLastProviderId = DEFAULT_PROVIDER_ID;
    }

    public int addCommandRegistry(CommandRegistry commandRegistry) {
        mLastProviderId++;
        mCommandsRegistries.put(mLastProviderId, commandRegistry);
        return mLastProviderId;
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
