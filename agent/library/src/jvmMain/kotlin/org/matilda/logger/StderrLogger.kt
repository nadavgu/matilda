package org.matilda.logger

class StderrLogger : Logger {
    override fun log(message: String) {
        System.err.println(message)
    }

    override fun log(message: String, throwable: Throwable) {
        System.err.println(message)
        throwable.printStackTrace()
    }
}
