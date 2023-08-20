package org.matilda.services.reflection;

import org.matilda.commands.MatildaCommand;
import org.matilda.commands.MatildaService;
import org.matilda.services.reflection.protobuf.MethodList;
import org.matilda.services.reflection.protobuf.MethodSpec;
import org.matilda.services.reflection.protobuf.ParameterType;

import javax.inject.Inject;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

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
    public MethodList getClassMethods(long id) {
        Class<?> clazz = (Class<?>) mObjectRepository.get(id);
        MethodList.Builder builder = MethodList.newBuilder();
        for (Method method : clazz.getDeclaredMethods()) {
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
            builder.addMethodsBuilder()
                    .setMethodId(mObjectRepository.add(method))
                    .setSpec(methodSpecBuilder);
        }

        return builder.build();
    }
}
