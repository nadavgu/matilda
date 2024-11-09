package org.matilda.messages.handlers

import org.matilda.messages.Message

fun interface MessageHandler {
    fun handle(message: Message)
}
