@file:OptIn(ExperimentalForeignApi::class)

package org.matilda.utils

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.toKString
import platform.linux.__NR_memfd_create
import platform.posix.errno
import platform.posix.strerror
import platform.posix.syscall

class ErrnoException(errno: Int) : Exception("Error in libc function: $errno ${strerror(errno)!!.toKString()}")

private fun checkResult(result: Long): Long {
    if (result == -1L) {
        throw ErrnoException(errno)
    }

    return result
}

fun memfdCreate(name: String, flags: Int): Fd {
    return checkResult(syscall(__NR_memfd_create.toLong(), name, flags)).toInt().asFd()
}