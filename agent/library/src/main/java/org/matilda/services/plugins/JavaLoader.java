package org.matilda.services.plugins;

import javax.inject.Inject;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

public class JavaLoader {
    @Inject
    JavaLoader() {}


    public ClassLoader load(byte[] jarBytes) throws IOException {
        Map<String, byte[]> classes = new HashMap<>();

        try (JarInputStream jarInputStream = new JarInputStream(new ByteArrayInputStream(jarBytes))) {
            JarEntry jarEntry;
            while ((jarEntry = jarInputStream.getNextJarEntry()) != null) {
                String entryName = jarEntry.getName();
                if (entryName.endsWith(".class")) {
                    // Extract the class name from the entry name
                    String className = entryName.replace('/', '.').replaceAll("\\.class$", "");
                    byte[] classData = readFully(jarInputStream);
                    classes.put(className, classData);
                }
            }
        }

        return new InMemoryClassLoader(classes);
    }

    private byte[] readFully(InputStream inputStream) throws IOException {
        byte[] buffer = new byte[4096];
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            byteArrayOutputStream.write(buffer, 0, bytesRead);
        }
        return byteArrayOutputStream.toByteArray();
    }
}
