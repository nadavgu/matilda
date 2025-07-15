package org.matilda.logger

class CompoundLogger(vararg loggers: Logger) : Logger {
    private val mLoggers = loggers
    override fun log(message: String) {
        mLoggers.forEach { it.log(message) }
    }

    override fun log(message: String, throwable: Throwable) {
        mLoggers.forEach { it.log(message, throwable) }
    }
}
