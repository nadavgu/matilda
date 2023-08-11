package org.matilda.commands.di;

import dagger.Module;
import dagger.Provides;
import org.matilda.commands.python.PythonProperties;
import org.matilda.commands.python.writer.PythonFileWriter;
import org.matilda.commands.utils.Package;

import javax.annotation.processing.ProcessingEnvironment;
import java.io.File;

import static org.matilda.commands.python.PythonProperties.PYTHON_GENERATED_PACKAGE_OPTION;
import static org.matilda.commands.python.PythonProperties.PYTHON_ROOT_DIR_OPTION;

@Module
public class PythonModule {
    @Provides
    PythonProperties pythonProperties(ProcessingEnvironment processingEnvironment) {
        File pythonRootDir = new File(processingEnvironment.getOptions().get(PYTHON_ROOT_DIR_OPTION));
        if (!pythonRootDir.exists()) {
            throw new RuntimeException("Specified python directory doesn't exist: " + pythonRootDir);
        }
        Package pythonGeneratedPackage =
                Package.fromString(processingEnvironment.getOptions().get(PYTHON_GENERATED_PACKAGE_OPTION));

        return new PythonProperties(pythonRootDir, pythonGeneratedPackage);
    }

    @Provides
    PythonFileWriter pythonFileWriter(PythonProperties pythonProperties) {
        return new PythonFileWriter(pythonProperties.pythonRootDir());
    }
}
