package org.matilda.commands;

import javax.inject.Inject;
import java.util.Random;

public class PluginDependencies {
    @Inject
    public CommandRegistryManager commandRegistryManager;

    @Inject
    public CommandRunner commandRunner;

    @Inject
    public Random random;

    @Inject
    public PluginDependencies() {
    }
}
