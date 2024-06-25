package org.matilda.commands.processors

import org.matilda.commands.info.ProjectServices
import org.matilda.commands.info.ServiceInfo

class ProjectDynamicServicesProcessor(private val mServiceProcessor: Processor<ServiceInfo>) : Processor<ProjectServices> {
    override fun process(instance: ProjectServices) {
        instance.forEachDynamicService(mServiceProcessor::process)
    }
}
