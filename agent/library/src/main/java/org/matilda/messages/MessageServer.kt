package org.matilda.messages

import me.tatarka.inject.annotations.Inject
import org.matilda.messages.handlers.MessageHandler
import java.io.EOFException

@Inject
class MessageServer(private val mMessageReceiver: MessageReceiver, private val mMessageHandler: MessageHandler) {
    suspend fun start() {
        while (true) {
            try {
                val message = mMessageReceiver.receive()
                mMessageHandler.handle(message)
            } catch (ignored: EOFException) {
                return
            }
        }
    }
}
