package org.matilda.di

import dagger.Module
import dagger.Provides
import java.util.*

@Module
class UtilsModule {
    @Provides
    fun random(): Random {
        return Random(System.currentTimeMillis())
    }
}
