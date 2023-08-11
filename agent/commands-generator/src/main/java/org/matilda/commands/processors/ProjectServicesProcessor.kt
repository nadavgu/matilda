package org.matilda.commands.processors;

import org.matilda.commands.info.ProjectServices;
import org.matilda.commands.info.ServiceInfo;

public class ProjectServicesProcessor implements Processor<ProjectServices> {
    private final Processor<ServiceInfo> mServiceProcessor;

    public ProjectServicesProcessor(Processor<ServiceInfo> servicesProcessor) {
        mServiceProcessor = servicesProcessor;
    }

    @Override
    public void process(ProjectServices projectServices) {
        projectServices.processEachService(mServiceProcessor);
    }
}
