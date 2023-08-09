package org.matilda.commands.processors;

import org.matilda.commands.info.ProjectServices;

import javax.inject.Inject;
import java.util.List;

public class ProcessorFactory {
    @Inject
    public ProcessorFactory() {}

    public Processor<ProjectServices> createProcessor() {
        return new CompoundProcessor<>(List.of());
    }
}
