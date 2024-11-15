package org.matilda.commands.processors

import org.matilda.commands.java.JavaProperties
import javax.inject.Inject

class ProcessorFactory @Inject constructor() {
    @Inject
    lateinit var mJavaRawCommandClassGenerator: JavaRawCommandClassGenerator

    @Inject
    lateinit var mKotlinRawCommandClassGenerator: KotlinRawCommandClassGenerator

    @Inject
    lateinit var mPythonRawCommandClassGenerator: PythonRawCommandClassGenerator

    @Inject
    lateinit var mCommandsModuleClassGenerator: CommandsModuleClassGenerator

    @Inject
    lateinit var mServicesModuleClassGenerator: ServicesModuleClassGenerator

    @Inject
    lateinit var mJavaServiceProxyClassGenerator: JavaServiceProxyClassGenerator

    @Inject
    lateinit var mKotlinServiceProxyClassGenerator: KotlinServiceProxyClassGenerator

    @Inject
    lateinit var mPythonServiceProxyClassGenerator: PythonServiceProxyClassGenerator

    @Inject
    lateinit var mPythonServiceInterfaceClassGenerator: PythonServiceInterfaceClassGenerator

    @Inject
    lateinit var mPythonServicesContainerClassGenerator: PythonServicesContainerClassGenerator

    @Inject
    lateinit var mJavaServiceDependenciesClassGenerator: JavaServiceDependenciesClassGenerator

    @Inject
    lateinit var mKotlinServiceDependenciesClassGenerator: KotlinServiceDependenciesClassGenerator

    @Inject
    lateinit var mPythonServiceDependenciesClassGenerator: PythonServiceDependenciesClassGenerator

    @Inject
    lateinit var mJavaCommandsRegistryFactoryClassGenerator: JavaCommandsRegistryFactoryClassGenerator

    @Inject
    lateinit var mKotlinCommandsRegistryFactoryClassGenerator: KotlinCommandsRegistryFactoryClassGenerator

    @Inject
    lateinit var mPythonCommandsRegistryFactoryClassGenerator: PythonCommandsRegistryFactoryClassGenerator

    @Inject
    lateinit var mJavaServiceProxyFactoryClassGenerator: JavaServiceProxyFactoryClassGenerator

    @Inject
    lateinit var mKotlinServiceProxyFactoryClassGenerator: KotlinServiceProxyFactoryClassGenerator

    @Inject
    lateinit var mPythonServiceProxyFactoryClassGenerator: PythonServiceProxyFactoryClassGenerator

    @Inject
    lateinit var mJavaDynamicServiceConverterClassGenerator: JavaDynamicServiceConverterClassGenerator

    @Inject
    lateinit var mKotlinDynamicServiceConverterClassGenerator: KotlinDynamicServiceConverterClassGenerator

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
            ProjectCommandsProcessor(selectGenerator(mJavaRawCommandClassGenerator, mKotlinRawCommandClassGenerator)),
            ProjectServicesProcessor(selectGenerator(mJavaServiceDependenciesClassGenerator, mKotlinServiceDependenciesClassGenerator)),
            ProjectServicesProcessor(mPythonServiceDependenciesClassGenerator),
            ProjectDynamicServicesProcessor(selectGenerator(mJavaServiceProxyClassGenerator, mKotlinServiceProxyClassGenerator)),
            ProjectServicesProcessor(mPythonServiceProxyClassGenerator),
            ProjectServicesProcessor(mPythonServiceInterfaceClassGenerator),
            ProjectServicesProcessor(selectGenerator(mJavaCommandsRegistryFactoryClassGenerator, mKotlinCommandsRegistryFactoryClassGenerator)),
            ProjectServicesProcessor(mPythonCommandsRegistryFactoryClassGenerator),
            ProjectDynamicServicesProcessor(selectGenerator(mJavaServiceProxyFactoryClassGenerator, mKotlinServiceProxyFactoryClassGenerator)),
            ProjectDynamicServicesProcessor(mPythonServiceProxyFactoryClassGenerator),
            ProjectDynamicServicesProcessor(selectGenerator(mJavaDynamicServiceConverterClassGenerator, mKotlinDynamicServiceConverterClassGenerator)),
            ProjectDynamicServicesProcessor(mPythonDynamicServiceConverterClassGenerator),
            OnlyRunOnceProcessor(mWasRun, mCommandsModuleClassGenerator),
            OnlyRunOnceProcessor(mWasRun, mServicesModuleClassGenerator),
            OnlyRunOnceProcessor(mWasRun, mPythonServicesContainerClassGenerator),
        )
    )
}
