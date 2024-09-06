package org.matilda.commands;

import javax.inject.Inject;

public class PluginDependencies {
    public final CommandRegistryManager commandRegistryManager;

    public final CommandRunner commandRunner;

    @Inject
    public PluginDependencies(CommandRegistryManager commandRegistryManager, CommandRunner commandRunner) {
        this.commandRegistryManager = commandRegistryManager;
        this.commandRunner = commandRunner;
    }
}
