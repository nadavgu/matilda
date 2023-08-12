package org.matilda.commands.protobuf

import java.io.File

data class ProtobufLocations(val projectProtobufDir: File, val googleProtobufDir: File) {
    companion object {
        const val PROJECT_PROTOBUF_DIR_OPTION = "projectProtobufDir"
        const val GOOGLE_PROTOBUF_DIR_OPTION = "googleProtobufDir"
    }
}
