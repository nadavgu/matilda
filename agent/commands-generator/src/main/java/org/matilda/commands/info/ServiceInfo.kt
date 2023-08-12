package org.matilda.commands.info

import javax.lang.model.type.TypeMirror

data class ServiceInfo(
    val fullName: String,
    val type: TypeMirror,
    val commands: MutableList<CommandInfo>,
    val hasInjectConstructor: Boolean
)
