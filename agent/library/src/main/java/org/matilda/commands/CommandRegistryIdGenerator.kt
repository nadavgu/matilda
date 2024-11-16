package org.matilda.commands

import me.tatarka.inject.annotations.Inject
import java.util.*

@Inject
class CommandRegistryIdGenerator(private val mRandom: Random) {
    fun generate() = mRandom.nextInt()
}
