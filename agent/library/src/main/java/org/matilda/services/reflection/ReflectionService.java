package org.matilda.services.reflection;

import org.matilda.commands.MatildaCommand;
import org.matilda.commands.MatildaService;
import org.matilda.services.reflection.protobuf.Method;
import org.matilda.services.reflection.protobuf.MethodSpec;
import org.matilda.services.reflection.protobuf.ParameterType;

import javax.inject.Inject;
import java.lang.reflect.Parameter;
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
    public List<org.matilda.services.reflection.protobuf.Method> getClassMethods(long id) {
        Class<?> clazz = (Class<?>) mObjectRepository.get(id);
        return Arrays.stream(clazz.getDeclaredMethods()).map(method -> {
            MethodSpec.Builder methodSpecBuilder = MethodSpec.newBuilder()
                    .setName(method.getName());
            for (Parameter parameter : method.getParameters()) {
                ParameterType.Builder parameterTypeBuilder = ParameterType.newBuilder();
                if (parameter.getType().isPrimitive()) {
                    parameterTypeBuilder.setPrimitiveClassName(parameter.getType().getCanonicalName());
                } else {
                    parameterTypeBuilder.setClassId(mObjectRepository.add(parameter.getType()));
                }
                methodSpecBuilder.addParameterTypes(parameterTypeBuilder);
            }
            return Method.newBuilder()
                    .setMethodId(mObjectRepository.add(method))
                    .setSpec(methodSpecBuilder)
                    .build();
        }).collect(Collectors.toList());
    }
}
