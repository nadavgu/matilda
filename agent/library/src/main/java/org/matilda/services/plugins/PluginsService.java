package org.matilda.services.plugins;

import org.matilda.commands.*;

import javax.inject.Inject;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@MatildaService
public class PluginsService {
    @Inject
    JavaLoader mJavaLoader;

    @Inject
    CommandRepository mCommandRepository;

    @Inject
    PluginDependencies mPluginDependencies;

    @Inject
    PluginsService() {}

    @MatildaCommand
    public int loadPlugin(byte[] jarBytes, String className) throws IOException, ClassNotFoundException,
            InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        ClassLoader classLoader = mJavaLoader.load(jarBytes);
        Class<?> entryPointClass = Class.forName(className, true, classLoader);
        Method commandRegistryMethod = entryPointClass.getDeclaredMethod("createCommandRegistry",
                PluginDependenciesModule.class);
        CommandRegistry commandRegistry =
                (CommandRegistry) commandRegistryMethod.invoke(null, new PluginDependenciesModule(mPluginDependencies));
        return mCommandRepository.addCommandRegistry(commandRegistry);
    }
}
