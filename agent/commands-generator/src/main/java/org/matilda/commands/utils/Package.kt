package org.matilda.commands.utils

import java.nio.file.FileSystems

class Package(parts: List<String>) {
    val parts: List<String>

    init {
        this.parts = parts.filter(String::isNotEmpty)
    }

    constructor(vararg parts: String) : this(listOf(*parts))

    fun subpackage(child: String): Package {
        return subpackage(fromString(child))
    }

    private fun subpackage(child: Package): Package {
        return joinPackages(this, child)
    }

    val packageName: String
        get() = parts.joinToString(".")
    val lastPart: String
        get() = parts.last()

    fun withoutLastPart() = Package(parts.dropLast(1))

    fun removeCommonPrefixFrom(other: Package): Package {
        var equalParts = 0
        while (equalParts < parts.size && equalParts < other.parts.size) {
            if (parts[equalParts] != other.parts[equalParts]) {
                break
            }
            equalParts++
        }
        return Package(parts.subList(equalParts, parts.size))
    }

    fun toPath() = parts.joinToString(FileSystems.getDefault().separator)

    companion object {
        @JvmStatic
        fun fromString(packageName: String) = Package(packageName.split("\\.".toRegex()))

        @JvmStatic
        fun joinPackages(vararg packages: Package) = Package(packages.flatMap(Package::parts))
    }
}
