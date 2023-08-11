package org.matilda.commands.processors

class OnlyRunOnceProcessor<T>(private val mWasRun: Boolean, private val mProcessor: Processor<T>) : Processor<T> {
    override fun process(instance: T) {
        if (!mWasRun) {
            mProcessor.process(instance)
        }
    }
}
