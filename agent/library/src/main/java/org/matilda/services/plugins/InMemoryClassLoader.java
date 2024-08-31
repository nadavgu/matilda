package org.matilda.services.plugins;

import java.util.Map;

public class InMemoryClassLoader extends ClassLoader {
    private final Map<String, byte[]> mClasses;

    public InMemoryClassLoader(Map<String, byte[]> classes) {
        mClasses = classes;
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        if (mClasses.containsKey(name)) {
            byte[] classData = mClasses.get(name);
            return defineClass(name, classData, 0, classData.length);
        } else {
            throw new ClassNotFoundException(name);
        }
    }
}
