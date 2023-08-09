package org.matilda.logger;

import java.util.Arrays;

public class StderrLogger implements Logger {

    @Override
    public void log(String message) {
        System.err.println(message);
    }

    @Override
    public void log(String message, Throwable throwable) {
        System.err.println(message);
        throwable.printStackTrace();
    }
}
