@file:OptIn(ExperimentalForeignApi::class)

package org.matilda.utils

import kotlinx.cinterop.*
import kotlinx.io.Buffer
import kotlinx.io.IOException
import kotlinx.io.RawSource
import platform.posix.errno
import platform.posix.read
import platform.posix.ssize_t

class FdSource(private val mFd: Fd, private val mOwn: Boolean = true) : RawSource {
    private var mClosed = false

    @OptIn(UnsafeNumber::class)
    override fun readAtMostTo(sink: Buffer, byteCount: Long): Long {
        val temporaryBuffer = ByteArray(byteCount.toInt())

        // Copy bytes from the file to the segment.
        val bytesRead = temporaryBuffer.usePinned { pinned ->
            read(mFd.fd, pinned.addressOf(0), byteCount.convert())
        }

        sink.write(temporaryBuffer, 0, bytesRead.toInt())

        return when {
            bytesRead > 0 -> bytesRead.convert()
            bytesRead == 0.convert<ssize_t>() -> -1L
            else -> throw IOException(errno.toString())
        }
    }

    override fun close() {
        if (mClosed or !mOwn) return
        mClosed = true
        mFd.close()
    }
}

fun Fd.source(own: Boolean = true) = FdSource(this, own)
