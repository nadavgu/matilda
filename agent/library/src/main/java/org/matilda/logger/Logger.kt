package org.matilda.logger

interface Logger {
    fun log(message: String)
    fun log(message: String, throwable: Throwable)
}
