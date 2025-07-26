package org.matilda.utils

import platform.linux.__NR_memfd_create

actual val memfdCreateSyscallNumber = __NR_memfd_create