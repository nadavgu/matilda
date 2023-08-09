package org.matilda.commands.di;

import dagger.Module;
import dagger.Provides;
import org.matilda.commands.info.ServiceInfo;
import org.matilda.commands.processors.Processor;
import org.matilda.commands.processors.ProcessorFactory;

@Module
public class ProcessorsModule {
    @Provides
    Processor<ServiceInfo> serviceProcessor(ProcessorFactory factory) {
        return factory.createServiceProcessor();
    }
}
