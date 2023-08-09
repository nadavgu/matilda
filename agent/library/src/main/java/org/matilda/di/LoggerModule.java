package org.matilda.di;

import dagger.Module;
import dagger.Provides;
import org.matilda.MatildaConnection;
import org.matilda.logger.CompoundLogger;
import org.matilda.logger.Logger;

import java.io.InputStream;
import java.io.OutputStream;

@Module
public class LoggerModule {
    private final Logger mLogger;

    public LoggerModule(Logger... loggers) {
        mLogger = new CompoundLogger(loggers);
    }

    @Provides
    Logger logger() {
        return mLogger;
    }
}
