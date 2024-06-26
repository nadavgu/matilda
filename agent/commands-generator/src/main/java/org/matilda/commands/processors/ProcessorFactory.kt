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
    lateinit var mPythonServiceProxyClassGenerator: PythonServiceProxyClassGenerator

    @Inject
    lateinit var mPythonServiceInterfaceClassGenerator: PythonServiceInterfaceClassGenerator

    @Inject
    lateinit var mPythonServicesContainerClassGenerator: PythonServicesContainerClassGenerator

    @Inject
    lateinit var mServiceDependenciesClassGenerator: ServiceDependenciesClassGenerator

    @Inject
    lateinit var mCommandsRegistryFactoryClassGenerator: CommandsRegistryFactoryClassGenerator

    @Inject
    lateinit var mJavaServiceProxyClassGenerator: JavaServiceProxyClassGenerator

    @Inject
    lateinit var mServiceProxyFactoryClassGenerator: ServiceProxyFactoryClassGenerator

    @set: Inject
    var mWasRun: Boolean = false

    fun createProcessor() = CompoundProcessor(
        listOf(
            ProjectCommandsProcessor(mRawCommandClassGenerator),
            ProjectServicesProcessor(mServiceDependenciesClassGenerator),
            OnlyRunOnceProcessor(mWasRun, mCommandsModuleClassGenerator),
            OnlyRunOnceProcessor(mWasRun, mServicesModuleClassGenerator),
            ProjectServicesProcessor(mPythonServiceProxyClassGenerator),
            ProjectServicesProcessor(mPythonServiceInterfaceClassGenerator),
            OnlyRunOnceProcessor(mWasRun, mPythonServicesContainerClassGenerator),
            ProjectServicesProcessor(mCommandsRegistryFactoryClassGenerator),
            ProjectDynamicServicesProcessor(mJavaServiceProxyClassGenerator),
            ProjectDynamicServicesProcessor(mServiceProxyFactoryClassGenerator),
        )
    )
}
