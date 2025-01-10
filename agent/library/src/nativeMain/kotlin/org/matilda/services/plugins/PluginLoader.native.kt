@file:OptIn(ExperimentalForeignApi::class)

package org.matilda.services.plugins

import kotlinx.cinterop.ExperimentalForeignApi

actual fun loadPlugin(bytes: ByteArray): Plugin {
    return NativePlugin(NativeLoader().load(bytes))
}