package org.matilda.messages

import org.matilda.messages.handlers.MessageHandler
import java.io.EOFException
import javax.inject.Inject

class MessageServer @Inject constructor() {
    @Inject
    lateinit var mMessageReceiver: MessageReceiver

    @Inject
    lateinit var mMessageHandler: MessageHandler
    fun start() {
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
