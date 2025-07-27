@file:OptIn(ExperimentalForeignApi::class)
package org.matilda.plugins.interop

import kotlinx.cinterop.*

fun CValue<CommandResultStruct>.toCommandResult() = useContents {
    CommandResult(success != 0, data.toByteArray())
}

fun CValue<CommandResultStruct>.free() = useContents {
    data.free()
}

fun <R> CValue<CommandResultStruct>.use(block: CValue<CommandResultStruct>.() -> R): R = try {
    block()
} finally {
    free()
}

fun CValue<CommandResultStruct>.moveToCommandResult() = use {
    toCommandResult()
}

fun CommandResult.toCommandResultStruct() = cValue<CommandResultStruct> {
    success = if (this@toCommandResultStruct.success) 1 else 0
    data.initializeFrom(this@toCommandResultStruct.data)
}

fun <R> CommandResult.tempCommandResultStruct(block: (CValue<CommandResultStruct>) -> R): R = toCommandResultStruct().use(block)