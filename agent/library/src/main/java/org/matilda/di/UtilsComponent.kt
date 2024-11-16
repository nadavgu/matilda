package org.matilda.di

import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.Provides
import java.util.*

@Component
interface UtilsComponent {
    @Provides
    fun random(): Random {
        return Random(System.currentTimeMillis())
    }
}
