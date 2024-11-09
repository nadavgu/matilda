package org.matilda.messages

interface MessageSender {
    fun send(message: Message)
}
