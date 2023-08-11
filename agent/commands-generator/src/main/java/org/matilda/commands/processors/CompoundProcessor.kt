package org.matilda.commands.processors

class CompoundProcessor<T>(private val mProcessors: List<Processor<T>>) : Processor<T> {
    override fun process(instance: T) {
        mProcessors.forEach { it.process(instance) }
    }
}
