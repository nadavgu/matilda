package org.matilda.services.plugins

import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.util.jar.JarEntry
import java.util.jar.JarInputStream
import dalvik.system.InMemoryDexClassLoader
import java.nio.ByteBuffer
import org.matilda.utils.collectDexFiles

actual fun loadJava(jarBytes: ByteArray): ClassLoader {
    return InMemoryDexClassLoader(collectDexFiles(jarBytes), ClassLoader.getSystemClassLoader())
}
