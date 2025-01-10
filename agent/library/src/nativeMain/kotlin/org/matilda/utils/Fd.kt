package org.matilda.utils

import platform.posix.close

class Fd(val fd: Int) : AutoCloseable {
    override fun close() {
        close(fd)
    }

    val procPath: String
        get() = "/proc/self/fd/$fd"
}

fun Int.asFd() = Fd(this)
