package org.matilda.services.plugins

import org.matilda.commands.*
import javax.inject.Inject

@MatildaService
class PluginsService @Inject internal constructor() {
    @Inject
    lateinit var mJavaLoader: JavaLoader

    @Inject
    lateinit var mCommandRepository: CommandRepository

    @Inject
    lateinit var mPluginDependencies: PluginDependencies
    @MatildaCommand
    fun loadPlugin(jarBytes: ByteArray, className: String): Int {
        val classLoader = mJavaLoader.load(jarBytes)
        val entryPointClass = Class.forName(className, true, classLoader)
        val commandRegistryMethod = entryPointClass.getDeclaredMethod(
            "createCommandRegistry",
            PluginDependenciesModule::class.java
        )
        val commandRegistry =
            commandRegistryMethod.invoke(null, PluginDependenciesModule(mPluginDependencies)) as CommandRegistry
        return mCommandRepository.addCommandRegistry(commandRegistry)
    }
}
