package org.matilda.commands.python.writer

import java.io.File
import java.io.IOException

class PythonFileWriter(private val mBaseFile: File) {
    fun write(pythonFile: PythonFile) {
        val file = getFile(pythonFile)
        ensureParentDirectoriesExist(file)
        file.writeText(pythonFile.content)
    }

    private fun getFile(pythonFile: PythonFile) = File(mBaseFile, pythonFile.packageName.toPath() + ".py")

    private fun ensureParentDirectoriesExist(file: File) {
        val parentFile = file.parentFile
        if (!parentFile.exists()) {
            if (!parentFile.mkdirs()) {
                throw IOException("Failed to ensure that directory exists: $parentFile")
            }
        }
    }
}
