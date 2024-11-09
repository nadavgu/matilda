package org.matilda.commands

import java.util.*
import javax.inject.Inject

class CommandIdGenerator @Inject internal constructor() {
    @Inject
    lateinit var mRandom: Random
    fun generate() = mRandom.nextInt()
}
