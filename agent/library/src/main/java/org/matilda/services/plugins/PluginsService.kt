package org.matilda.services.plugins

import me.tatarka.inject.annotations.Inject
import org.matilda.commands.*

@MatildaService
class PluginsService @Inject constructor(private val mJavaLoader: JavaLoader,
                                         private val mCommandRepository: CommandRepository,
                                         private val mPluginDependencies: PluginDependencies) {
    @MatildaCommand
    fun loadPlugin(jarBytes: ByteArray, className: String): Int {
        val classLoader = mJavaLoader.load(jarBytes)
        val entryPointClass = Class.forName(className, true, classLoader)
        val commandRegistryMethod = entryPointClass.getDeclaredMethod(
            "createCommandRegistry",
            PluginDependencies::class.java
        )
        val commandRegistry =
            commandRegistryMethod.invoke(null, mPluginDependencies) as CommandRegistry
        return mCommandRepository.addCommandRegistry(commandRegistry)
    }
}
