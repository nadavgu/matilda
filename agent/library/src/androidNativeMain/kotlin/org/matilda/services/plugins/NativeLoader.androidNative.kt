@file:OptIn(ExperimentalForeignApi::class)

package org.matilda.services.plugins

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.cValue
import kotlinx.cinterop.convert
import org.matilda.utils.Fd
import platform.android.ANDROID_DLEXT_FORCE_LOAD
import platform.android.ANDROID_DLEXT_USE_LIBRARY_FD
import platform.android.android_dlextinfo
import platform.android.android_dlopen_ext
import platform.posix.RTLD_LAZY

actual fun dlopenFd(fd: Fd) = android_dlopen_ext("plugin", RTLD_LAZY, cValue<android_dlextinfo> {
        library_fd = fd.fd
        flags = (ANDROID_DLEXT_USE_LIBRARY_FD or ANDROID_DLEXT_FORCE_LOAD).convert()
    })