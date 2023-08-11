package org.matilda.commands;

import org.matilda.commands.collectors.ServicesCollector;
import org.matilda.commands.info.ProjectServices;
import org.matilda.commands.processors.Processor;

import javax.inject.Inject;

public class CommandsGenerator {
    @Inject
    ServicesCollector mServicesCollector;

    @Inject
    Processor<ProjectServices> mProjectServicesProcessor;

    @Inject
    CommandsGenerator() {}

    public void generate() {
        ProjectServices services = mServicesCollector.collect();
        mProjectServicesProcessor.process(services);
    }
}
