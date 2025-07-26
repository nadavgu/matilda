@file:OptIn(ExperimentalForeignApi::class)

package org.matilda.utils

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.UnsafeNumber
import kotlinx.cinterop.convert
import kotlinx.cinterop.toKString
import platform.posix.errno
import platform.posix.ssize_t
import platform.posix.strerror
import platform.posix.syscall

class ErrnoException(errno: Int) : Exception("Error in libc function: $errno ${strerror(errno)!!.toKString()}")

@OptIn(UnsafeNumber::class)
private fun checkResult(result: ssize_t): ssize_t {
    if (result == (-1).convert<ssize_t>()) {
        throw ErrnoException(errno)
    }

    return result
}

@OptIn(UnsafeNumber::class)
fun memfdCreate(name: String, flags: UInt): Fd {
    return checkResult(syscall(memfdCreateSyscallNumber.convert<ssize_t>(), name, flags)).toInt().asFd()
}

expect val memfdCreateSyscallNumber: Int