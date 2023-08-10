package org.matilda.commands.processors;

public class OnlyRunOnceProcessor<T> implements Processor<T> {
    private final boolean mWasRun;
    private final Processor<T> mProcessor;

    public OnlyRunOnceProcessor(boolean wasRun, Processor<T> processor) {
        mWasRun = wasRun;
        mProcessor = processor;
    }

    @Override
    public void process(T instance) {
        if (!mWasRun) {
            mProcessor.process(instance);
        }
    }
}
