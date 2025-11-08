package org.matilda.utils

import kotlinx.io.buffered
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem
import kotlinx.io.readLine

private fun processProcStatusEntries(callback: (String, String) -> Unit) {
    SystemFileSystem.source(Path("/proc/self/status")).buffered().use { statusSource ->
        while (true) {
            val line = statusSource.readLine() ?: break
            val (key, value) = line.split(":").map { it.trim() }
            callback(key, value)
        }
    }
}

fun getProcessStatusEntry(key: String): String {
    var result: String? = null
    processProcStatusEntries { entryKey, entryValue ->
        if (key == entryKey) {
            result = entryValue
        }
    }

    return result!!
}

fun getTracerId(): Int {
    return getProcessStatusEntry("TracerPid").toInt()
}