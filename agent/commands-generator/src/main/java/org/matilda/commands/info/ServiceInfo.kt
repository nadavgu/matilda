package org.matilda.commands.info

import androidx.room.compiler.processing.XType

data class ServiceInfo(
    val fullName: String,
    val type: XType,
    val commands: MutableList<CommandInfo>,
) {
    override fun toString() = fullName
}
