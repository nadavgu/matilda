@file:OptIn(ExperimentalForeignApi::class)

package org.matilda.services.plugins

import kotlinx.cinterop.*
import platform.posix.dlsym

class NativeLibrary(private val mHandle : CPointer<out CPointed>) {
    fun <T : Function<*>> findFunction(name: String) = findSymbol<CFunction<T>>(name)
    private fun <T : CPointed> findSymbol(name: String) = checkLibdlResult(dlsym(mHandle, name)).reinterpret<T>()
}