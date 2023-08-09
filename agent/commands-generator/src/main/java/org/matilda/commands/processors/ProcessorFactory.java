package org.matilda.commands.processors;

import org.matilda.commands.info.ServiceInfo;

import javax.inject.Inject;
import java.util.List;

public class ProcessorFactory {
    @Inject
    public ProcessorFactory() {}

    public Processor<ServiceInfo> createServiceProcessor() {
        return new CompoundProcessor<>(List.of());
    }
}
