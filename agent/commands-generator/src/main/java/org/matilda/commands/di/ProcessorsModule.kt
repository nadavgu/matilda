package org.matilda.commands.di;

import dagger.Module;
import dagger.Provides;
import org.matilda.commands.info.ProjectServices;
import org.matilda.commands.processors.Processor;
import org.matilda.commands.processors.ProcessorFactory;

@Module
public class ProcessorsModule {
    @Provides
    Processor<ProjectServices> serviceProcessor(ProcessorFactory factory) {
        return factory.createProcessor();
    }
}
