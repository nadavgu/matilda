package org.matilda.commands.info

import javax.lang.model.type.TypeMirror

data class CommandInfo(
    val name: String,
    val service: ServiceInfo,
    val parameters: List<ParameterInfo>,
    val returnType: TypeMirror,
    val thrownTypes: List<TypeMirror>,
)
