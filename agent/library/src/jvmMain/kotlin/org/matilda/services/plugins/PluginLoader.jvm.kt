package org.matilda.services.plugins

actual fun loadPlugin(bytes: ByteArray): Plugin {
    return JavaPlugin(JavaLoader().load(bytes))
}