package org.matilda.commands.processors

import org.matilda.commands.info.ProjectServices
import javax.inject.Inject

class ProcessorFactory @Inject constructor() {
    @Inject
    lateinit var mRawCommandClassGenerator: RawCommandClassGenerator

    @Inject
    lateinit var mCommandsModuleClassGenerator: CommandsModuleClassGenerator

    @Inject
    lateinit var mServicesModuleClassGenerator: ServicesModuleClassGenerator

    @Inject
    lateinit var mPythonServiceClassGenerator: PythonServiceClassGenerator

    @set: Inject
    var mWasRun: Boolean = false

    fun createProcessor(): Processor<ProjectServices> {
        return CompoundProcessor(
            listOf(
                ProjectCommandsProcessor(mRawCommandClassGenerator),
                OnlyRunOnceProcessor(mWasRun, mCommandsModuleClassGenerator),
                OnlyRunOnceProcessor(mWasRun, mServicesModuleClassGenerator),
                ProjectServicesProcessor(mPythonServiceClassGenerator)
            )
        )
    }
}
