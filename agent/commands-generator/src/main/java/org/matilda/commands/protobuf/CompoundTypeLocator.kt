package org.matilda.commands.protobuf

import com.squareup.javapoet.ClassName
import org.matilda.commands.utils.Package

class CompoundTypeLocator(private val mLocators: List<Pair<Package, ProtobufTypeLocator>>) : ProtobufTypeLocator {
    override fun locate(className: ClassName) =
        mLocators.firstNotNullOfOrNull { (basePackage, locator) ->
            val type = locator.locate(className)
            if (type != null) {
                ProtobufType(basePackage.subpackage(type.typePackage), type.typeName)
            } else {
                null
            }
        }
}