@file:OptIn(ExperimentalForeignApi::class)

package org.matilda.services.plugins

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.toKString
import platform.posix.dlerror

class DlException(dlerror: String): Exception("Error in libdl function: $dlerror")

fun <T> checkLibdlResult(result: T?): T {
    if (result == null) {
        throw DlException(dlerror()!!.toKString())
    }
    return result
}