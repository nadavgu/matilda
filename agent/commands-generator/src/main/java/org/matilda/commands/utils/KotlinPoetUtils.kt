package org.matilda.commands.utils

import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.TypeSpec

fun fileSpecBuilder(packageName: String, typeSpec: TypeSpec) = FileSpec.Companion.builder(packageName, typeSpec.name!!)
    .addType(typeSpec)
