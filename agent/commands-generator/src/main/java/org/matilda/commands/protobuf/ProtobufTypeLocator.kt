package org.matilda.commands.protobuf

import com.squareup.javapoet.ClassName

interface ProtobufTypeLocator {
    fun locate(className: ClassName): ProtobufType?
}
