@file:OptIn(ExperimentalForeignApi::class)

package org.matilda.services.plugins

import kotlinx.cinterop.ExperimentalForeignApi
import org.matilda.utils.Fd
import platform.posix.RTLD_LAZY
import platform.posix.dlopen

actual fun dlopenFd(fd: Fd) = dlopen(fd.procPath, RTLD_LAZY)