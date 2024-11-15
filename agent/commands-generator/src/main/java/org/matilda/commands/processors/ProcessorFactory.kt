package org.matilda.commands.processors

import org.matilda.commands.java.JavaProperties
import javax.inject.Inject

class ProcessorFactory @Inject constructor() {
    @Inject
    lateinit var mJavaRawCommandClassGenerator: JavaRawCommandClassGenerator
    @Inject
    lateinit var mPythonRawCommandClassGenerator: PythonRawCommandClassGenerator

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
    lateinit var mJavaCommandsRegistryFactoryClassGenerator: JavaCommandsRegistryFactoryClassGenerator

    @Inject
    lateinit var mPythonCommandsRegistryFactoryClassGenerator: PythonCommandsRegistryFactoryClassGenerator

    @Inject
    lateinit var mJavaServiceProxyFactoryClassGenerator: JavaServiceProxyFactoryClassGenerator

    @Inject
    lateinit var mPythonServiceProxyFactoryClassGenerator: PythonServiceProxyFactoryClassGenerator

    @Inject
    lateinit var mJavaDynamicServiceConverterClassGenerator: JavaDynamicServiceConverterClassGenerator

    @Inject
    lateinit var mPythonDynamicServiceConverterClassGenerator: PythonDynamicServiceConverterClassGenerator

    @set: Inject
    var mWasRun: Boolean = false

    @Inject
    lateinit var mJavaProperties: JavaProperties

    private val mShouldGenerateKotlin
        get() = mJavaProperties.shouldGenerateKotlin

    private fun <T> selectGenerator(javaProcessor: T, kotlinProcessor: T) = if (mShouldGenerateKotlin) {
        kotlinProcessor
    } else {
        javaProcessor
    }

    fun createProcessor() = CompoundProcessor(
        listOf(
            ProjectCommandsProcessor(mJavaRawCommandClassGenerator),
            ProjectCommandsProcessor(mPythonRawCommandClassGenerator),
            ProjectServicesProcessor(mJavaServiceDependenciesClassGenerator),
            ProjectServicesProcessor(mPythonServiceDependenciesClassGenerator),
            ProjectDynamicServicesProcessor(mJavaServiceProxyClassGenerator),
            ProjectServicesProcessor(mPythonServiceProxyClassGenerator),
            ProjectServicesProcessor(mPythonServiceInterfaceClassGenerator),
            ProjectServicesProcessor(mJavaCommandsRegistryFactoryClassGenerator),
            ProjectServicesProcessor(mPythonCommandsRegistryFactoryClassGenerator),
            ProjectDynamicServicesProcessor(mJavaServiceProxyFactoryClassGenerator),
            ProjectDynamicServicesProcessor(mPythonServiceProxyFactoryClassGenerator),
            ProjectDynamicServicesProcessor(mJavaDynamicServiceConverterClassGenerator),
            ProjectDynamicServicesProcessor(mPythonDynamicServiceConverterClassGenerator),
            OnlyRunOnceProcessor(mWasRun, mCommandsModuleClassGenerator),
            OnlyRunOnceProcessor(mWasRun, mServicesModuleClassGenerator),
            OnlyRunOnceProcessor(mWasRun, mPythonServicesContainerClassGenerator),
        )
    )
}
