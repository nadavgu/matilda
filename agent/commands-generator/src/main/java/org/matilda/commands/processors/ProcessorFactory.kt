package org.matilda.commands.processors

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

    @Inject
    lateinit var mPythonServicesContainerClassGenerator: PythonServicesContainerClassGenerator

    @set: Inject
    var mWasRun: Boolean = false

    fun createProcessor() = CompoundProcessor(
        listOf(
            ProjectCommandsProcessor(mRawCommandClassGenerator),
            OnlyRunOnceProcessor(mWasRun, mCommandsModuleClassGenerator),
            OnlyRunOnceProcessor(mWasRun, mServicesModuleClassGenerator),
            ProjectServicesProcessor(mPythonServiceClassGenerator),
            OnlyRunOnceProcessor(mWasRun, mPythonServicesContainerClassGenerator),
        )
    )
}
