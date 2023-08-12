package org.matilda.commands.protobuf

import java.io.File

data class ProtobufLocations(val projectProtobufDir: File, val apiProtobufDir: File, val googleProtobufDir: File) {
    companion object {
        const val PROJECT_PROTOBUF_DIR_OPTION = "projectProtobufDir"
        const val API_PROTOBUF_DIR_OPTION = "apiProtobufDir"
        const val GOOGLE_PROTOBUF_DIR_OPTION = "googleProtobufDir"
    }
}
