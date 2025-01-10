package org.matilda.di

import kotlinx.datetime.Clock
import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.Provides
import java.util.*

@Component
interface UtilsComponent {
    @Provides
    fun random(): Random {
        return Random(Clock.System.now().toEpochMilliseconds())
    }
}
