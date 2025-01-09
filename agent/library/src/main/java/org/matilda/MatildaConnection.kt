package org.matilda

import kotlinx.io.RawSink
import kotlinx.io.RawSource

class MatildaConnection(val source: RawSource, val sink: RawSink) {
    fun close() {
        try {
            sink.close()
        } catch (ignored: Throwable) {
        }
        try {
            source.close()
        } catch (ignored: Throwable) {
        }
    }
}
