package org.matilda.services.plugins

fun loadPlugin(bytes: ByteArray): Plugin {
    return JavaPlugin(JavaLoader().load(bytes))
}