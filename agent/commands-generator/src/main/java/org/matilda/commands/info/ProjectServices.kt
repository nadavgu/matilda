package org.matilda.commands.info

import org.matilda.commands.processors.Processor

class ProjectServices(private val mServices: List<ServiceInfo>) {
    fun processEachService(processor: Processor<ServiceInfo>) {
        mServices.forEach(processor::process)
    }

    fun processEachCommand(processor: Processor<CommandInfo>) {
        mServices.forEach { it.commands.forEach(processor::process) }
    }
}
