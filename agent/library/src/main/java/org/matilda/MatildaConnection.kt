package org.matilda

import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

class MatildaConnection(val inputStream: InputStream, val outputStream: OutputStream) {
    fun close() {
        try {
            outputStream.close()
        } catch (ignored: IOException) {
        }
        try {
            inputStream.close()
        } catch (ignored: IOException) {
        }
    }
}
