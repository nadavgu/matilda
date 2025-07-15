package org.matilda.logger

import org.matilda.utils.printErr

class StderrLogger : Logger {
    override fun log(message: String) {
        printErr(message)
    }

    override fun log(message: String, throwable: Throwable) {
        printErr(message)
        throwable.printStackTrace()
    }
}
