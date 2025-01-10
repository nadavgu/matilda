@file:OptIn(ExperimentalForeignApi::class)

package org.matilda.utils

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import kotlinx.io.Buffer
import kotlinx.io.IOException
import kotlinx.io.RawSource
import platform.posix.close
import platform.posix.errno
import platform.posix.read

class FdSource(private val mFd: Int) : RawSource {
    private var closed = false

    override fun readAtMostTo(sink: Buffer, byteCount: Long): Long {
        val temporaryBuffer = ByteArray(byteCount.toInt())

        // Copy bytes from the file to the segment.
        val bytesRead = temporaryBuffer.usePinned { pinned ->
            read(mFd, pinned.addressOf(0), byteCount.toULong())
        }

        sink.write(temporaryBuffer, 0, bytesRead.toInt())

        return when {
            bytesRead > 0 -> bytesRead
            bytesRead == 0L -> -1L
            else -> throw IOException(errno.toString())
        }
    }

    override fun close() {
        if (closed) return
        closed = true
        close(mFd)
    }
}

fun Int.fdSource() = FdSource(this)
