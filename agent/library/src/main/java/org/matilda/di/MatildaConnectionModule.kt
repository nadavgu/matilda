package org.matilda.di

import dagger.Module
import dagger.Provides
import org.matilda.MatildaConnection
import java.io.InputStream
import java.io.OutputStream

@Module
class MatildaConnectionModule(private val mMatildaConnection: MatildaConnection) {
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
