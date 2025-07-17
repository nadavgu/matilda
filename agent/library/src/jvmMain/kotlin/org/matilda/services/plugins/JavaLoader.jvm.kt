package org.matilda.services.plugins

import org.matilda.utils.collectClasses

actual fun loadJava(jarBytes: ByteArray): ClassLoader {
    val classes = collectClasses(jarBytes)
    return InMemoryClassLoader(classes)
}
