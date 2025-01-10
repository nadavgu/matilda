package org.matilda

import kotlinx.cinterop.ExperimentalForeignApi
import org.matilda.logger.StderrLogger
import org.matilda.utils.Fd
import org.matilda.utils.sink
import org.matilda.utils.source
import platform.posix.*

fun main() {
    val logger = StderrLogger()
    try {
        pingToLoader()
        MatildaAgent(MatildaConnection(Fd(STDIN_FILENO).source(), Fd(STDOUT_FILENO).sink()), logger).run()
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
