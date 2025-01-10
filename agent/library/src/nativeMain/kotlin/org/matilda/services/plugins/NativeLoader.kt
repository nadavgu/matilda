@file:OptIn(ExperimentalForeignApi::class)

package org.matilda.services.plugins

import kotlinx.cinterop.CPointed
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.io.Buffer
import kotlinx.io.RawSink
import org.matilda.utils.Fd
import org.matilda.utils.memfdCreate
import org.matilda.utils.sink
import platform.posix.RTLD_LAZY
import platform.posix.dlopen

class NativeLoader {
    fun load(bytes: ByteArray): CPointer<out CPointed> {
        return checkLibdlResult(memfdCreate("plugin", 0).use { fd ->
            fd.write(bytes)
            dlopen(fd.procPath, RTLD_LAZY)
        })
    }

    fun Fd.write(bytes: ByteArray) {
        sink(own = false).use {
            it.write(bytes)
        }
    }

    private fun RawSink.write(bytes: ByteArray) {
        val buffer = Buffer()
        buffer.write(bytes)
        write(buffer, bytes.size.toLong())
    }
}