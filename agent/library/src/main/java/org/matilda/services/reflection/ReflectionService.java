package org.matilda.services.reflection;

import org.matilda.commands.MatildaCommand;
import org.matilda.commands.MatildaService;
import org.matilda.services.reflection.protobuf.JavaValue;
import org.matilda.services.reflection.protobuf.ParameterType;

import javax.inject.Inject;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
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

    @MatildaCommand
    public boolean isMethodStatic(long id) {
        Method method = (Method) mObjectRepository.get(id);
        return (method.getModifiers() & Modifier.STATIC) != 0;
    }

    @MatildaCommand
    public JavaValue invokeMethod(long methodId, long objectId, List<JavaValue> arguments) throws InvocationTargetException, IllegalAccessException {
        return invokeMethod(mObjectRepository.get(objectId), methodId, arguments);
    }

    @MatildaCommand
    public JavaValue invokeStaticMethod(long methodId, List<JavaValue> arguments) throws InvocationTargetException, IllegalAccessException {
        return invokeMethod(null, methodId, arguments);
    }


    private JavaValue invokeMethod(Object receiver, long methodId, List<JavaValue> arguments) throws InvocationTargetException, IllegalAccessException {
        Method method = (Method) mObjectRepository.get(methodId);
        Parameter[] parameters = method.getParameters();
        if (parameters.length != arguments.size()) {
            throw new IllegalArgumentException();
        }

        Object[] objectArguments = new Object[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            objectArguments[i] = fromJavaValue(parameters[i].getType(), arguments.get(i));
        }

        return toJavaValue(method.invoke(receiver, objectArguments));
    }

    private JavaValue toJavaValue(Object object) {
        if (object == null) {
            return JavaValue.newBuilder().build();
        } if (object instanceof Integer) {
            return JavaValue.newBuilder().setInt((Integer) object).build();
        } else if (object instanceof Long) {
            return JavaValue.newBuilder().setInt((Long) object).build();
        } else if (object instanceof Boolean) {
            return JavaValue.newBuilder().setBool((Boolean) object).build();
        } else if (object instanceof Double) {
            return JavaValue.newBuilder().setFloat((Double) object).build();
        } else if (object instanceof Float) {
            return JavaValue.newBuilder().setFloat((Float) object).build();
        } else {
            return JavaValue.newBuilder().setObjectId(mObjectRepository.add(object)).build();
        }
    }

    private Object fromJavaValue(Class<?> type, JavaValue javaValue) {
        switch (javaValue.getValueCase()) {
            case INT: {
                if (type.equals(byte.class) || type.equals(Byte.class)) {
                    return (byte) javaValue.getInt();
                } else if (type.equals(short.class) || type.equals(Short.class)) {
                    return (short) javaValue.getInt();
                } else if (type.equals(int.class) || type.equals(Integer.class)) {
                    return (int) javaValue.getInt();
                } else if (type.equals(long.class) || type.equals(Long.class)) {
                    return javaValue.getInt();
                } else {
                    throw new IllegalArgumentException(String.valueOf(javaValue.getInt()));
                }
            }
            case BOOL: return javaValue.getBool();
            case FLOAT: {
                if (type.equals(float.class) || type.equals(Float.class)) {
                    return (float) javaValue.getFloat();
                } else if (type.equals(double.class) || type.equals(Double.class)) {
                    return javaValue.getFloat();
                } else {
                    throw new IllegalArgumentException(String.valueOf(javaValue.getFloat()));
                }
            }
            case OBJECT_ID: return mObjectRepository.get(javaValue.getObjectId());
            default: return null;
        }
    }
}
