package org.matilda.commands.processors

interface Processor<T> {
    fun process(instance: T)
}
