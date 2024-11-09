package org.matilda.commands.protobuf

import androidx.room.compiler.codegen.XClassName

interface ProtobufTypeLocator {
    fun locate(className: XClassName): ProtobufType?
}
