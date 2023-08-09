package org.matilda.commands;

import org.matilda.commands.collectors.ServicesCollector;
import org.matilda.commands.info.ServiceInfo;
import org.matilda.commands.processors.Processor;

import javax.inject.Inject;

public class CommandsGenerator {
    @Inject
    ServicesCollector mServicesCollector;

    @Inject
    Processor<ServiceInfo> mServiceProcessor;

    @Inject
    CommandsGenerator() {}

    public void generate() {
        mServicesCollector.collect().processEachService(mServiceProcessor);
    }
}
