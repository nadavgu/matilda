package org.matilda.services.reflection;

import org.matilda.commands.MatildaCommand;
import org.matilda.commands.MatildaService;

import javax.inject.Inject;

@MatildaService
public class ReflectionService {
    @Inject
    ObjectRepository mObjectRepository;

    @Inject
    public ReflectionService() {}

    @MatildaCommand
    public long findClass(String className) throws ClassNotFoundException {
        Class<?> clazz = Class.forName(className);
        return mObjectRepository.add(clazz);
    }

    @MatildaCommand
    public String getClassName(long id) {
        Class<?> clazz = (Class<?>) mObjectRepository.get(id);
        return clazz.getCanonicalName();
    }
}
