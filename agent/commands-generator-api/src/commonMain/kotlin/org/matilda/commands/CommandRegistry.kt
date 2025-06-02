package org.matilda.commands

class CommandRegistry {
    private val mCommands = mutableMapOf<Int, Command>()

    fun addCommand(type: Int, command: Command) {
        if (mCommands.containsKey(type)) {
            throw RuntimeException("Command $type already handled by ${mCommands[type]}")
        }
        mCommands[type] = command
    }

    operator fun get(type: Int): Command =
        mCommands.getOrElse(type) {
            throw IllegalArgumentException("Command $type not found")
        }

    val commands: Map<Int, Command>
        get() = mCommands
}
