package org.matilda.commands;

import org.matilda.commands.collectors.ServicesCollector;

import javax.inject.Inject;

public class CommandsGenerator {
    @Inject
    ServicesCollector mServicesCollector;

    @Inject
    CommandsGenerator() {}

    public void generate() {
        mServicesCollector.collect();
    }
}
