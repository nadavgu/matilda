package org.matilda.commands.processors

import org.matilda.commands.info.ProjectServices
import org.matilda.commands.info.ServiceInfo

class ProjectDynamicInterfacesProcessor(private val mServiceProcessor: Processor<ServiceInfo>) : Processor<ProjectServices> {
    override fun process(instance: ProjectServices) {
        instance.forEachDynamicInterface(mServiceProcessor::process)
    }
}
