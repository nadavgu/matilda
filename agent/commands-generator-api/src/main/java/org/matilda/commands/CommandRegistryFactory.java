package org.matilda.commands;

public interface CommandRegistryFactory<T> {
    CommandRegistry createCommandRegistry(T service);
}
