package org.matilda.commands

import me.tatarka.inject.annotations.Inject
import kotlin.random.Random

@Inject
class CommandRegistryIdGenerator(private val mRandom: Random) {
    fun generate() = mRandom.nextInt()
}
