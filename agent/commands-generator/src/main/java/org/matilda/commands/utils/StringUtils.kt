package org.matilda.commands.utils

fun String.toSnakeCase(): String {
    val regex = "([a-z])([A-Z]+)".toRegex()

    // Replacement string
    val replacement = "$1_$2"

    // Replace the given regex
    // with replacement string
    // and convert it to lower case.
    return replace(regex, replacement).lowercase()
}