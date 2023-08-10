package org.matilda.commands.processors;

import org.matilda.commands.info.CommandInfo;
import org.matilda.commands.info.ProjectServices;

public class ProjectCommandsProcessor implements Processor<ProjectServices> {
    private final Processor<CommandInfo> mCommandProcessor;

    public ProjectCommandsProcessor(Processor<CommandInfo> commandProcessor) {
        mCommandProcessor = commandProcessor;
    }

    @Override
    public void process(ProjectServices projectServices) {
        projectServices.processEachCommand(mCommandProcessor);
    }
}
