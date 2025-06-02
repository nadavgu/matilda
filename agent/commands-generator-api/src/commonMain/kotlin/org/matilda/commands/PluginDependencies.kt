package org.matilda.commands

import me.tatarka.inject.annotations.Inject

@Inject
class PluginDependencies(val commandRegistryManager: CommandRegistryManager, val commandRunner: CommandRunner)
