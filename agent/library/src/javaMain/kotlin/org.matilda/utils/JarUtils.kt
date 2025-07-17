package org.matilda.utils

import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.nio.ByteBuffer
import java.util.jar.JarEntry
import java.util.jar.JarInputStream

fun collectClasses(jarBytes: ByteArray): Map<String, ByteArray> {
    val classes: MutableMap<String, ByteArray> = HashMap()

    collectJarEntries(jarBytes) { it.name.endsWith(".class") }
        .forEach { (entry, contents) ->
            val className = entry.name.replace('/', '.').replace("\\.class$".toRegex(), "")
            classes[className] = contents
        }

    return classes
}

fun collectDexFiles(jarBytes: ByteArray): Array<ByteBuffer> {
    return collectJarEntries(jarBytes) { it.name.endsWith(".dex") }
        .map { (_, contents) -> ByteBuffer.wrap(contents) }
        .toTypedArray<ByteBuffer>()
}

private fun collectJarEntries(jarBytes: ByteArray, filter: (JarEntry) -> Boolean): List<Pair<JarEntry, ByteArray>> {
    val entries = mutableListOf<Pair<JarEntry, ByteArray>>()
    JarInputStream(ByteArrayInputStream(jarBytes)).use { jarInputStream ->
        var jarEntry: JarEntry
        while (jarInputStream.nextJarEntry.also { jarEntry = it } != null) {
            if (filter(jarEntry)) {
                entries.add(jarEntry to readFully(jarInputStream))
            }
        }
    }

    return entries
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
