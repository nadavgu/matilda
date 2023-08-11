package org.matilda.commands.processors;

import org.matilda.commands.info.ProjectServices;

import javax.inject.Inject;
import java.util.List;

public class ProcessorFactory {
    @Inject
    public ProcessorFactory() {}

    @Inject
    RawCommandClassGenerator mRawCommandClassGenerator;

    @Inject
    CommandsModuleClassGenerator mCommandsModuleClassGenerator;

    @Inject
    ServicesModuleClassGenerator mServicesModuleClassGenerator;

    @Inject
    PythonServiceClassGenerator mPythonServiceClassGenerator;

    @Inject
    boolean mWasRun;

    public Processor<ProjectServices> createProcessor() {
        return new CompoundProcessor<>(List.of(
                new ProjectCommandsProcessor(mRawCommandClassGenerator),
                new OnlyRunOnceProcessor<>(mWasRun, mCommandsModuleClassGenerator),
                new OnlyRunOnceProcessor<>(mWasRun, mServicesModuleClassGenerator),
                new ProjectServicesProcessor(mPythonServiceClassGenerator)
        ));
    }
}
