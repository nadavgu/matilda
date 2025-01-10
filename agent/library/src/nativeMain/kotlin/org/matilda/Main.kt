package org.matilda

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem
import org.matilda.logger.StderrLogger
import platform.posix.*

fun main() {
    pingToLoader()
    MatildaAgent(MatildaConnection(STDIN_FILENO.fdSource(), STDOUT_FILENO.fdSink()), StderrLogger()).run()
}

@OptIn(ExperimentalForeignApi::class)
private fun pingToLoader() {
    fputc(0, stdout)
    fflush(stdout)
}

private fun Int.fdSource() = SystemFileSystem.source(fdPath)
private fun Int.fdSink() = SystemFileSystem.sink(fdPath)

private val Int.fdPath
    get() = Path("/proc/self/fd/$this")
