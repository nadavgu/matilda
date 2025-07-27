@file:OptIn(ExperimentalForeignApi::class)
package org.matilda.plugins.interop

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.StableRef
import kotlinx.cinterop.asStableRef
import kotlinx.cinterop.staticCFunction
import org.matilda.commands.CommandRunner

fun CommandRunnerStruct.toCommandRunner() = NativeCommandRunner(run!!, handle!!)

fun CommandRunnerStruct.initializeFrom(commandRunner: CommandRunner) {
    val stableRef = StableRef.create(commandRunner)
    handle = stableRef.asCPointer()

    run = staticCFunction { handle, registryId, commandType, parameterStruct ->
        handle!!.asStableRef<CommandRunner>().get()
            .runCatching(registryId, commandType, parameterStruct.toByteArray()).toCommandResultStruct()
    }
}
