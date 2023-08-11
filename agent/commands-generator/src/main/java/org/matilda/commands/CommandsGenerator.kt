package org.matilda.commands

import org.matilda.commands.collectors.ServicesCollector
import org.matilda.commands.info.ProjectServices
import org.matilda.commands.processors.Processor
import javax.inject.Inject

class CommandsGenerator @Inject constructor() {
    @Inject
    lateinit var mServicesCollector: ServicesCollector

    @Inject
    lateinit var mProjectServicesProcessor: Processor<ProjectServices>

    fun generate() {
        val services = mServicesCollector.collect()
        mProjectServicesProcessor.process(services)
    }
}
