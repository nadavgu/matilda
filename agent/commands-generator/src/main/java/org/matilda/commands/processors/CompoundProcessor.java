package org.matilda.commands.processors;

import java.util.List;

public class CompoundProcessor<T> implements Processor<T> {
    private final List<Processor<T>> mProcessors;

    public CompoundProcessor(List<Processor<T>> processors) {
        mProcessors = processors;
    }

    @Override
    public void process(T instance) {
        mProcessors.forEach(processor -> processor.process(instance));
    }
}
