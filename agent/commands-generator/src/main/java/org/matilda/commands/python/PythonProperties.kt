package org.matilda.commands.python

import org.matilda.commands.utils.Package
import java.io.File

data class PythonProperties(val pythonRootDir: File, val pythonGeneratedPackage: Package,
                            val generatedProtobufPackage: Package) {
    companion object {
        const val PYTHON_ROOT_DIR_OPTION = "pythonRootDir"
        const val PYTHON_GENERATED_PACKAGE_OPTION = "pythonGeneratedPackage"
        const val GENERATED_PROTO_SUBPACKAGE_OPTION = "generatedProtoSubpackage"
    }
}
