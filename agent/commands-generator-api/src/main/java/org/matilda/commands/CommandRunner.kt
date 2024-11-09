package org.matilda.commands

interface CommandRunner {
    fun run(registryId: Int, commandType: Int, parameter: ByteArray): ByteArray
}
