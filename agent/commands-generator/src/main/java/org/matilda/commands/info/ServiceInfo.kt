package org.matilda.commands.info

import javax.lang.model.type.TypeMirror

data class ServiceInfo(
    val fullName: String,
    val type: TypeMirror,
    val commands: List<CommandInfo>,
    val hasInjectConstructor: Boolean
) {
    override fun toString() = fullName
}
