package org.matilda.commands

import java.util.*
import javax.inject.Inject

class PluginDependencies @Inject constructor() {
    @Inject
    lateinit var commandRegistryManager: CommandRegistryManager

    @Inject
    lateinit var commandRunner: CommandRunner

    @Inject
    lateinit var random: Random
}
