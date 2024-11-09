package org.matilda.services.plugins

import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.util.jar.JarEntry
import java.util.jar.JarInputStream
import javax.inject.Inject

class JavaLoader @Inject internal constructor() {
    fun load(jarBytes: ByteArray): ClassLoader {
        val classes: MutableMap<String, ByteArray> = HashMap()
        JarInputStream(ByteArrayInputStream(jarBytes)).use { jarInputStream ->
            var jarEntry: JarEntry
            while (jarInputStream.nextJarEntry.also { jarEntry = it } != null) {
                val entryName = jarEntry.name
                if (entryName.endsWith(".class")) {
                    // Extract the class name from the entry name
                    val className = entryName.replace('/', '.').replace("\\.class$".toRegex(), "")
                    val classData = readFully(jarInputStream)
                    classes[className] = classData
                }
            }
        }
        return InMemoryClassLoader(classes)
    }

    @Throws(IOException::class)
    private fun readFully(inputStream: InputStream): ByteArray {
        val buffer = ByteArray(4096)
        val byteArrayOutputStream = ByteArrayOutputStream()
        var bytesRead: Int
        while (inputStream.read(buffer).also { bytesRead = it } != -1) {
            byteArrayOutputStream.write(buffer, 0, bytesRead)
        }
        return byteArrayOutputStream.toByteArray()
    }
}
