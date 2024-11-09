package org.matilda.di

import dagger.Module
import dagger.Provides
import org.matilda.logger.CompoundLogger
import org.matilda.logger.Logger

@Module
class LoggerModule(vararg loggers: Logger) {
    private val mLogger = CompoundLogger(*loggers)


    @Provides
    fun logger(): Logger {
        return mLogger
    }
}
