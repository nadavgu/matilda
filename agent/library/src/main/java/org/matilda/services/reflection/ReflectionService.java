package org.matilda.services.reflection;

import org.matilda.commands.MatildaCommand;
import org.matilda.commands.MatildaService;
import org.matilda.services.reflection.protobuf.JavaValue;
import org.matilda.services.reflection.protobuf.JavaType;

import javax.inject.Inject;
import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@MatildaService
public class ReflectionService {
    @Inject
    ReflectionUtils mReflectionUtils;

    @Inject
    public ReflectionService() {}

    @MatildaCommand
    public long findClass(String className) throws ClassNotFoundException {
        Class<?> clazz = Class.forName(className);
        return mReflectionUtils.register(clazz);
    }

    @MatildaCommand
    public String getClassName(long id) {
        Class<?> clazz = mReflectionUtils.getClass(id);
        return clazz.getCanonicalName();
    }

    @MatildaCommand
    public List<Long> getClassMethods(long id) {
        Class<?> clazz = mReflectionUtils.getClass(id);
        return Arrays.stream(clazz.getDeclaredMethods()).map(mReflectionUtils::register).collect(Collectors.toList());
    }

    @MatildaCommand
    public long getSuperclass(long classId) {
        Class<?> superClass = mReflectionUtils.getClass(classId).getSuperclass();
        return mReflectionUtils.register(superClass);
    }

    @MatildaCommand
    public String getMethodName(long id) {
        return mReflectionUtils.getMethod(id).getName();
    }

    @MatildaCommand
    public List<JavaType> getMethodParameterTypes(long id) {
        Method method = mReflectionUtils.getMethod(id);
        return Arrays.stream(method.getParameters())
                .map(Parameter::getType)
                .map(mReflectionUtils::toJavaType)
                .collect(Collectors.toList());
    }

    @MatildaCommand
    public long getMethod(long classId, String methodName, List<JavaType> parameterTypes)
            throws NoSuchMethodException, ClassNotFoundException {
        Class<?> clazz = mReflectionUtils.getClass(classId);
        List<Class<?>> list = new ArrayList<>();
        for (JavaType parameterType : parameterTypes) {
            Class<?> parameterClass = mReflectionUtils.fromJavaType(parameterType);
            list.add(parameterClass);
        }
        return mReflectionUtils.register(clazz.getDeclaredMethod(methodName, list.toArray(new Class<?>[0])));
    }

    @MatildaCommand
    public boolean isMethodStatic(long id) {
        Method method = mReflectionUtils.getMethod(id);
        return (method.getModifiers() & Modifier.STATIC) != 0;
    }

    @MatildaCommand
    public JavaValue invokeMethod(long methodId, long objectId, List<JavaValue> arguments) throws InvocationTargetException, IllegalAccessException {
        return invokeMethod(mReflectionUtils.getObject(objectId), methodId, arguments);
    }

    @MatildaCommand
    public JavaValue invokeStaticMethod(long methodId, List<JavaValue> arguments) throws InvocationTargetException, IllegalAccessException {
        return invokeMethod(null, methodId, arguments);
    }


    private JavaValue invokeMethod(Object receiver, long methodId, List<JavaValue> arguments) throws InvocationTargetException, IllegalAccessException {
        Method method = mReflectionUtils.getMethod(methodId);
        Parameter[] parameters = method.getParameters();
        if (parameters.length != arguments.size()) {
            throw new IllegalArgumentException();
        }

        Object[] objectArguments = new Object[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            objectArguments[i] = mReflectionUtils.fromJavaValue(parameters[i].getType(), arguments.get(i));
        }

        return mReflectionUtils.toJavaValue(method.getReturnType(), method.invoke(receiver, objectArguments));
    }

    @MatildaCommand
    public List<Long> getClassFields(long id) {
        Class<?> clazz = mReflectionUtils.getClass(id);
        return Arrays.stream(clazz.getDeclaredFields()).map(mReflectionUtils::register).collect(Collectors.toList());
    }

    @MatildaCommand
    public String getFieldName(long id) {
        return mReflectionUtils.getField(id).getName();
    }

    @MatildaCommand
    public JavaType getFieldType(long id) {
        return mReflectionUtils.toJavaType(mReflectionUtils.getField(id).getType());
    }

    @MatildaCommand
    public long getField(long classId, String fieldName) throws NoSuchFieldException {
        return mReflectionUtils.register(mReflectionUtils.getClass(classId).getDeclaredField(fieldName));
    }

    @MatildaCommand
    public boolean isFieldStatic(long id) {
        return (mReflectionUtils.getField(id).getModifiers() & Modifier.STATIC) != 0;
    }

    @MatildaCommand
    public JavaValue getFieldValue(long fieldId, long objectId) throws IllegalAccessException {
        return getFieldValue(fieldId, mReflectionUtils.getObject(objectId));
    }

    @MatildaCommand
    public JavaValue getStaticFieldValue(long fieldId) throws IllegalAccessException {
        return getFieldValue(fieldId, null);
    }

    private JavaValue getFieldValue(long fieldId, Object object) throws IllegalAccessException {
        Field field = mReflectionUtils.getField(fieldId);
        field.setAccessible(true);
        return mReflectionUtils.toJavaValue(field.getType(), field.get(object));
    }

    @MatildaCommand
    public void setFieldValue(long fieldId, long objectId, JavaValue newValue) throws IllegalAccessException {
        setFieldValue(fieldId, mReflectionUtils.getObject(objectId), newValue);
    }

    @MatildaCommand
    public void setStaticFieldValue(long fieldId, JavaValue newValue) throws IllegalAccessException {
        setFieldValue(fieldId, null, newValue);
    }

    private void setFieldValue(long fieldId, Object object, JavaValue newValue) throws IllegalAccessException {
        Field field = mReflectionUtils.getField(fieldId);
        field.setAccessible(true);
        field.set(object, mReflectionUtils.fromJavaValue(field.getType(), newValue));
    }
}
