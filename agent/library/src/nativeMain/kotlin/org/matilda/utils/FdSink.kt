@file:OptIn(ExperimentalForeignApi::class)

package org.matilda.utils

import kotlinx.cinterop.*
import kotlinx.io.Buffer
import kotlinx.io.IOException
import kotlinx.io.RawSink
import kotlinx.io.readByteArray
import platform.posix.*


private class FileSink(
    private val file: CPointer<FILE>
) : RawSink {
    private var closed = false

    override fun write(
        source: Buffer,
        byteCount: Long
    ) {
        require(byteCount >= 0L) { "byteCount: $byteCount" }
        require(source.size >= byteCount) { "source.size=${source.size} < byteCount=$byteCount" }
        check(!closed) { "closed" }

        val allContent = source.readByteArray(byteCount.toInt())
        // Copy bytes from that segment into the file.
        val bytesWritten = allContent.usePinned { pinned ->
            fwrite(pinned.addressOf(0), 1u, byteCount.toUInt().convert(), file).toLong()
        }
        if (bytesWritten < byteCount) {
            throw IOException(errno.toString())
        }
    }

    override fun flush() {
        if (fflush(file) != 0) {
            throw IOException(errno.toString())
        }
    }

    override fun close() {
        if (closed) return
        closed = true
        if (fclose(file) != 0) {
            throw IOException(errno.toString())
        }
    }
}

fun Int.fdSink(): RawSink {
    val file = fdopen(this, "wb")
        ?: throw IOException("Failed to open fd $this with ${strerror(errno)?.toKString()}")
    return FileSink(file)
}