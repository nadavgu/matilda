package org.matilda.commands.processors

import org.matilda.commands.info.CommandInfo
import org.matilda.commands.info.ProjectServices

class ProjectCommandsProcessor(private val mCommandProcessor: Processor<CommandInfo>) : Processor<ProjectServices> {
    override fun process(instance: ProjectServices) {
        instance.forEachCommand(mCommandProcessor::process)
    }
}
