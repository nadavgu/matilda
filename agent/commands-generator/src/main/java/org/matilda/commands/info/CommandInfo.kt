package org.matilda.commands.info

import javax.lang.model.type.TypeMirror

data class CommandInfo(
    val name: String,
    val service: ServiceInfo,
    val parameterType: TypeMirror,
    val returnType: TypeMirror
)
