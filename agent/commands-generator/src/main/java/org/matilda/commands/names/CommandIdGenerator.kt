package org.matilda.commands.names

import org.matilda.commands.info.CommandInfo
import java.nio.ByteBuffer
import java.security.MessageDigest
import javax.inject.Inject

class CommandIdGenerator @Inject constructor() {
    fun generate(commandInfo: CommandInfo): Int {
        val digest = MessageDigest.getInstance("SHA-1")
        digest.update(commandInfo.service.fullName.toByteArray())
        digest.update(commandInfo.name.toByteArray())
        val hashBytes = digest.digest()
        return ByteBuffer.wrap(hashBytes).getInt()
    }
}
