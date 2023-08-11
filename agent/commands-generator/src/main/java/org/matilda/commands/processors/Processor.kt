package org.matilda.commands.processors;

public interface Processor<T> {
    void process(T instance);
}
