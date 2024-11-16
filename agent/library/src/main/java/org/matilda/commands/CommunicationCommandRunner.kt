package org.matilda.commands

import me.tatarka.inject.annotations.Inject
import org.matilda.commands.listener.CommandResponseListener


class CommunicationCommandRunner
@Inject internal constructor(private val mCommandSender: CommandSender,
                             private val mCommandResponseListener: CommandResponseListener,
                             private val mCommandIdGenerator: CommandIdGenerator) : CommandRunner {
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
