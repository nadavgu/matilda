package org.matilda.plugins.interop

import org.matilda.commands.Command
import org.matilda.commands.CommandFailedException
import org.matilda.commands.CommandRunner

data class CommandResult(val success: Boolean, val data: ByteArray) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as CommandResult

        if (success != other.success) return false
        if (!data.contentEquals(other.data)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = success.hashCode()
        result = 31 * result + data.contentHashCode()
        return result
    }

    fun checkResult(): ByteArray {
        if (!success) {
            throw CommandFailedException(data.decodeToString())
        }

        return data
    }
}

fun Command.runCatching(param: ByteArray): CommandResult {
    return try {
        CommandResult(true, run(param))
    } catch (e: Throwable) {
        CommandResult(false, e.stackTraceToString().encodeToByteArray())
    }
}

fun CommandRunner.runCatching(registryId: Int, commandType: Int, parameter: ByteArray): CommandResult {
    return try {
        CommandResult(true, run(registryId, commandType, parameter))
    } catch (e: Throwable) {
        CommandResult(false, e.stackTraceToString().encodeToByteArray())
    }
}
