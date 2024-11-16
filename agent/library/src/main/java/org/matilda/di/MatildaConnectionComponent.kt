package org.matilda.di

import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.Provides
import org.matilda.MatildaConnection
import java.io.InputStream
import java.io.OutputStream

@Component
abstract class MatildaConnectionComponent(private val mMatildaConnection: MatildaConnection) {
    @Provides
    fun connection(): MatildaConnection {
        return mMatildaConnection
    }

    @Provides
    fun inputStream(): InputStream {
        return mMatildaConnection.inputStream
    }

    @Provides
    fun outputStream(): OutputStream {
        return mMatildaConnection.outputStream
    }
}
