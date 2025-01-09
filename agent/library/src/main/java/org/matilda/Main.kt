package org.matilda

import kotlinx.io.asSink
import kotlinx.io.asSource
import org.matilda.logger.StderrLogger

object Main {
    @JvmStatic
    fun main(args: Array<String>) {
        pingToLoader()
        MatildaAgent(MatildaConnection(System.`in`.asSource(), System.out.asSink()), StderrLogger()).run()
    }

    private fun pingToLoader() {
        System.out.write(0)
        System.out.flush()
    }
}