package org.matilda.di

import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.Provides
import org.matilda.logger.CompoundLogger
import org.matilda.logger.Logger

@Component
abstract class LoggerComponent(loggers: Array<out Logger>) {
    private val mLogger = CompoundLogger(*loggers)


    @Provides
    fun logger(): Logger {
        return mLogger
    }
}
