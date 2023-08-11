package org.matilda.commands.python.writer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class PythonFileWriter {
    private final File mBaseFile;

    public PythonFileWriter(File baseFile) {
        mBaseFile = baseFile;
    }

    public void write(PythonFile pythonFile) throws IOException {
        File file = getFile(pythonFile);
        ensureParentDirectoriesExist(file);
        try (FileWriter fileWriter = new FileWriter(file)) {
            fileWriter.write(pythonFile.getContent());
        }
    }

    private File getFile(PythonFile pythonFile) {
        return new File(mBaseFile, pythonFile.getPackage().toPath() + ".py");
    }

    private void ensureParentDirectoriesExist(File file) throws IOException {
        File parentFile = file.getParentFile();
        if (!parentFile.exists()) {
            if (!parentFile.mkdirs()) {
                throw new IOException("Failed to ensure that directory exists: " + parentFile);
            }
        }
    }
}
