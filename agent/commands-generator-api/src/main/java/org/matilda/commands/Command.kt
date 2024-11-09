package org.matilda.commands

interface Command {
    @Throws(Throwable::class)
    fun run(parameter: ByteArray): ByteArray
}
