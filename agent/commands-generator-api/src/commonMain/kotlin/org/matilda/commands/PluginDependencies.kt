package org.matilda.commands

import me.tatarka.inject.annotations.Inject
import kotlin.random.Random

@Inject
class PluginDependencies(val commandRegistryManager: CommandRegistryManager, val commandRunner: CommandRunner,
                         val random: Random
)
