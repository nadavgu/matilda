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
    lateinit var mJavaServiceProxyClassGenerator: JavaServiceProxyClassGenerator

    @Inject
    lateinit var mPythonServiceProxyClassGenerator: PythonServiceProxyClassGenerator

    @Inject
    lateinit var mPythonServiceInterfaceClassGenerator: PythonServiceInterfaceClassGenerator

    @Inject
    lateinit var mPythonServicesContainerClassGenerator: PythonServicesContainerClassGenerator

    @Inject
    lateinit var mJavaServiceDependenciesClassGenerator: JavaServiceDependenciesClassGenerator

    @Inject
    lateinit var mPythonServiceDependenciesClassGenerator: PythonServiceDependenciesClassGenerator

    @Inject
    lateinit var mCommandsRegistryFactoryClassGenerator: CommandsRegistryFactoryClassGenerator

    @Inject
    lateinit var mServiceProxyFactoryClassGenerator: ServiceProxyFactoryClassGenerator

    @Inject
    lateinit var mDynamicServiceConverterClassGenerator: DynamicServiceConverterClassGenerator

    @set: Inject
    var mWasRun: Boolean = false

    fun createProcessor() = CompoundProcessor(
        listOf(
            ProjectCommandsProcessor(mRawCommandClassGenerator),
            ProjectServicesProcessor(mJavaServiceDependenciesClassGenerator),
            ProjectServicesProcessor(mPythonServiceDependenciesClassGenerator),
            ProjectDynamicServicesProcessor(mJavaServiceProxyClassGenerator),
            ProjectServicesProcessor(mPythonServiceProxyClassGenerator),
            ProjectServicesProcessor(mPythonServiceInterfaceClassGenerator),
            ProjectServicesProcessor(mCommandsRegistryFactoryClassGenerator),
            ProjectDynamicServicesProcessor(mServiceProxyFactoryClassGenerator),
            ProjectDynamicServicesProcessor(mDynamicServiceConverterClassGenerator),
            OnlyRunOnceProcessor(mWasRun, mCommandsModuleClassGenerator),
            OnlyRunOnceProcessor(mWasRun, mServicesModuleClassGenerator),
            OnlyRunOnceProcessor(mWasRun, mPythonServicesContainerClassGenerator),
        )
    )
}
