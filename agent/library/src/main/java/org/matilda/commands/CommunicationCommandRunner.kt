package org.matilda.commands

import org.matilda.commands.listener.CommandResponseListener
import javax.inject.Inject

class CommunicationCommandRunner @Inject internal constructor() : CommandRunner {
    @Inject
    lateinit var mCommandSender: CommandSender

    @Inject
    lateinit var mCommandResponseListener: CommandResponseListener

    @Inject
    lateinit var mCommandIdGenerator: CommandIdGenerator
    override fun run(registryId: Int, commandType: Int, parameter: ByteArray): ByteArray {
        val commandId = mCommandIdGenerator.generate()
        try {
            mCommandResponseListener.listen(commandId).use { listeningInstance ->
                mCommandSender.send(registryId, commandType, commandId, parameter)
                return listeningInstance.waitForResponse()
            }
        } catch (e: InterruptedException) {
            Thread.currentThread().interrupt()
            throw RuntimeException(e)
        }
    }
}
