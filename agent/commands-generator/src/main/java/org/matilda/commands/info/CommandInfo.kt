package org.matilda.commands.info

import androidx.room.compiler.codegen.XTypeName
import androidx.room.compiler.processing.XType

data class CommandInfo(
    val name: String,
    val service: ServiceInfo,
    val parameters: List<ParameterInfo>,
    val returnType: XType,
    val thrownTypes: List<XType>,
)

fun CommandInfo.hasReturnValue(): Boolean {
    return returnType.asTypeName() != XTypeName.UNIT_VOID
}
