package org.matilda

import org.matilda.logger.StderrLogger

object Main {
    @JvmStatic
    fun main(args: Array<String>) {
        pingToLoader()
        MatildaAgent(MatildaConnection(System.`in`, System.out), StderrLogger()).run()
    }

    private fun pingToLoader() {
        System.out.write(0)
        System.out.flush()
    }
}