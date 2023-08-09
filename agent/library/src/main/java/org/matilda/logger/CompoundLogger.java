package org.matilda.logger;

import java.util.Arrays;

public class CompoundLogger implements Logger {
    private final Logger[] mLoggers;

    public CompoundLogger(Logger... loggers) {
        mLoggers = loggers;
    }

    @Override
    public void log(String message) {
        Arrays.stream(mLoggers).forEach(logger -> logger.log(message));
    }

    @Override
    public void log(String message, Throwable throwable) {
        Arrays.stream(mLoggers).forEach(logger -> logger.log(message, throwable));
    }
}
