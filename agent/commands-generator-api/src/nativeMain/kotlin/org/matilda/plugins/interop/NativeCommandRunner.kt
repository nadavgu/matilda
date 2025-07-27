@file:OptIn(ExperimentalForeignApi::class)

package org.matilda.plugins.interop

import kotlinx.cinterop.*
import org.matilda.commands.CommandRunner

class NativeCommandRunner(
    private val mFunction: CPointer<CFunction<(COpaquePointer?, Int, Int, CValue<ByteArrayStruct>) -> CValue<CommandResultStruct>>>,
    private val mInstance: COpaquePointer) : CommandRunner {
    override fun run(registryId: Int, commandType: Int, parameter: ByteArray): ByteArray {
        return parameter.tempByteArrayStruct { parameterStruct ->
            mFunction(mInstance, registryId, commandType, parameterStruct).moveToCommandResult().checkResult()
        }
    }
}