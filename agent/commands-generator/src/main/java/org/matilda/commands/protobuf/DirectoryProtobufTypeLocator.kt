package org.matilda.commands.protobuf

import com.squareup.javapoet.ClassName
import org.matilda.commands.utils.Package
import java.io.File

class DirectoryProtobufTypeLocator(private val mBaseDir: File) : ProtobufTypeLocator {
    override fun locate(className: ClassName): ProtobufType? {
        val file = findContainingFile(className) ?: return null
        return ProtobufType(calculatePackage(file), className.simpleName())
    }

    private fun findContainingFile(className: ClassName): File? {
        return mBaseDir.walk().find {
            it.isFile && it.extension == "proto" && it.containsProtobufJavaClass(className)
        }
    }

    private fun calculatePackage(file: File): Package {
        return Package.fromPath(file.parentFile.toRelativeString(mBaseDir)).subpackage(file.nameWithoutExtension)
    }

    private fun File.containsProtobufJavaClass(className: ClassName): Boolean {
        val javaPackageLine = "option java_package = \"${className.packageName()}\";"

        val lines = readLines()
        return lines.contains(javaPackageLine) && lines.any { it.definesProtobufType(className.simpleName()) }
    }

    private fun String.definesProtobufType(typeName: String): Boolean {
        return listOf("message", "enum").any {
            this == "$it $typeName {"
        }
    }
}
