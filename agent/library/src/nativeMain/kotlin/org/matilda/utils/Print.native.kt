package org.matilda.utils

import kotlinx.cinterop.ExperimentalForeignApi
import platform.posix.fprintf
import platform.posix.stderr

@OptIn(ExperimentalForeignApi::class)
actual fun printErr(message: String) {
    fprintf(stderr, "%s\n", message)
}