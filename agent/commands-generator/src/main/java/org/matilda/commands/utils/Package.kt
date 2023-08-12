package org.matilda.commands.utils

import java.io.File

data class Package(val parts: List<String>) {
    constructor(vararg parts: String) : this(listOf(*parts))

    fun subpackage(child: String) = subpackage(fromString(child))

    fun subpackage(child: Package) = joinPackages(this, child)

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

    fun toPath() = parts.joinToString(File.separator)

    companion object {
        fun fromString(packageName: String) = Package(packageName.split("\\.".toRegex()))

        fun joinPackages(vararg packages: Package) = Package(packages.flatMap(Package::parts))
        fun fromPath(path: String): Package = Package(path.split(File.separator).filter { it.isNotEmpty() })
    }
}
