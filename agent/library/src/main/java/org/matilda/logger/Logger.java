package org.matilda.logger;

public interface Logger {
    void log(String message);
    void log(String message, Throwable throwable);

}
