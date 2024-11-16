package org.matilda.commands

import me.tatarka.inject.annotations.Inject
import java.util.*

@Inject
class PluginDependencies(val commandRegistryManager: CommandRegistryManager, val commandRunner: CommandRunner,
                         val random: Random)
