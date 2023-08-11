package org.matilda.commands.python;

import org.matilda.commands.utils.Package;

import java.io.File;

public record PythonProperties(File pythonRootDir, Package pythonGeneratedPackage) {
    public static final String PYTHON_ROOT_DIR_OPTION = "pythonRootDir";
    public static final String PYTHON_GENERATED_PACKAGE_OPTION = "pythonGeneratedPackage";
}
