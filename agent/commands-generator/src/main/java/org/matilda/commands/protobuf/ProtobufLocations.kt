package org.matilda.commands.protobuf

import java.io.File

data class ProtobufLocations(val locations: List<File>) {
    companion object {
        const val PROTOBUF_DIRS_OPTION = "protobufDirs"
    }
}
