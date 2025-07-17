package org.matilda.services.plugins

actual fun loadPlugin(bytes: ByteArray): Plugin {
    return JavaPlugin(loadJava(bytes))
}

expect fun loadJava(jarBytes: ByteArray): ClassLoader