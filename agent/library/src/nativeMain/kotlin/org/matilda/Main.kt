package org.matilda

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem
import org.matilda.logger.StderrLogger
import platform.posix.*

fun main() {
    val logger = StderrLogger()
    try {
        pingToLoader()
        MatildaAgent(MatildaConnection(STDIN_FILENO.fdSource(), STDOUT_FILENO.fdSink()), logger).run()
    } catch (e: Throwable) {
        // Handle exception here on purpose, cause kotlin-native's uncaught exception handler writes to stdout,
        // but we don't want to write there because we use it
        logger.log("Error running Matilda agent", e)
        exit(-1)
    }
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
