package org.matilda.services.reflection;

import org.matilda.commands.MatildaCommand;
import org.matilda.commands.MatildaService;
import org.matilda.services.reflection.protobuf.ParameterType;

import javax.inject.Inject;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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

    @MatildaCommand
    public List<Long> getClassMethods(long id) {
        Class<?> clazz = (Class<?>) mObjectRepository.get(id);
        return Arrays.stream(clazz.getDeclaredMethods()).map(mObjectRepository::add).collect(Collectors.toList());
    }

    @MatildaCommand
    public String getMethodName(long id) {
        Method method = (Method) mObjectRepository.get(id);
        return method.getName();
    }

    @MatildaCommand
    public List<ParameterType> getMethodParameterTypes(long id) {
        Method method = (Method) mObjectRepository.get(id);
        return Arrays.stream(method.getParameters()).map(parameter -> {
            ParameterType.Builder parameterTypeBuilder = ParameterType.newBuilder();
            if (parameter.getType().isPrimitive()) {
                parameterTypeBuilder.setPrimitiveClassName(parameter.getType().getCanonicalName());
            } else {
                parameterTypeBuilder.setClassId(mObjectRepository.add(parameter.getType()));
            }
            return parameterTypeBuilder.build();
        }).collect(Collectors.toList());
    }
}
