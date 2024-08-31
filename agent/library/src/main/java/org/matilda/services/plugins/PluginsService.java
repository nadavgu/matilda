package org.matilda.services.plugins;

import org.matilda.commands.CommandRegistry;
import org.matilda.commands.CommandRepository;
import org.matilda.commands.MatildaCommand;
import org.matilda.commands.MatildaService;

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
    PluginsService() {}

    @MatildaCommand
    public int loadPlugin(byte[] jarBytes, String className) throws IOException, ClassNotFoundException,
            InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        ClassLoader classLoader = mJavaLoader.load(jarBytes);
        Class<?> entryPointClass = Class.forName(className, true, classLoader);
        Method commandRegistryMethod = entryPointClass.getDeclaredMethod("createCommandRegistry");
        CommandRegistry commandRegistry = (CommandRegistry) commandRegistryMethod.invoke(null);
        return mCommandRepository.addCommandRegistry(commandRegistry);
    }
}
