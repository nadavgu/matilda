package org.matilda.commands.utils

import com.squareup.javapoet.ClassName
import com.squareup.kotlinpoet.FileSpec

val PBANDK_MESSAGE_EXTENSIONS_TYPE = ClassName.get("pbandk", "MessageKt")
val PBANDK_ANY_EXTENSIONS_EXTENSIONS_TYPE = ClassName.get("pbandk", "AnyExtensionsKt")

fun FileSpec.Builder.addPbandkExtensionImports() =
    addImport("pbandk", "decodeFromByteArray")
    .addImport("pbandk", "pack")
    .addImport("pbandk", "encodeToByteArray")
