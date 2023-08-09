package org.matilda.commands.info;

import org.matilda.commands.processors.Processor;

import java.util.List;

public class ProjectServices {
    private final List<ServiceInfo> mServices;

    public ProjectServices(List<ServiceInfo> services) {
        mServices = services;
    }

    public void processEachService(Processor<ServiceInfo> processor) {
        mServices.forEach(processor::process);
    }
}
