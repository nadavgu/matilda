package org.matilda.messages.handlers

import org.matilda.messages.Message

fun interface MessageHandler {
    suspend fun handle(message: Message)
}
