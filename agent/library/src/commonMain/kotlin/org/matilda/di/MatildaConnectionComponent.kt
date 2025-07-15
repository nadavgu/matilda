package org.matilda.di

import kotlinx.io.*
import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.Provides
import org.matilda.MatildaConnection

@Component
abstract class MatildaConnectionComponent(private val mMatildaConnection: MatildaConnection) {
    @Provides
    fun connection(): MatildaConnection {
        return mMatildaConnection
    }

    @Provides
    fun rawSource(): RawSource {
        return mMatildaConnection.source
    }

    @Provides
    fun source(rawSource: RawSource): Source {
        return rawSource.buffered()
    }

    @Provides
    fun rawSink(): RawSink {
        return mMatildaConnection.sink
    }

    @Provides
    fun sink(rawSink: RawSink): Sink {
        return rawSink.buffered()
    }
}
