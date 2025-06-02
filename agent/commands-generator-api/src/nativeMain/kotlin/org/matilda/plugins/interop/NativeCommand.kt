@file:OptIn(ExperimentalForeignApi::class)

package org.matilda.plugins.interop

import kotlinx.cinterop.*
import org.matilda.commands.Command

typealias NativeCommandFunction = CFunction<(COpaquePointer?, CValue<ByteArrayStruct>) -> CValue<ByteArrayStruct>>

class NativeCommand(private val mFunction: CPointer<NativeCommandFunction>,
                    private val mInstance: COpaquePointer) : Command {
    override fun run(parameter: ByteArray): ByteArray {
        return parameter.tempByteArrayStruct { parameterStruct ->
            mFunction(mInstance, parameterStruct).moveToByteArray()
        }
    }
}