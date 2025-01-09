package org.matilda.commands

import me.tatarka.inject.annotations.Inject
import kotlin.random.Random

class CommandIdGenerator @Inject internal constructor(private val mRandom: Random) {
    fun generate() = mRandom.nextInt()
}
