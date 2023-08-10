package org.matilda.commands;

import com.google.protobuf.Int32Value;

import javax.inject.Inject;

@MatildaService
public class MathService {
    @Inject
    MathService() {}

    @MatildaCommand
    public Int32Value square(Int32Value number) {
        return Int32Value.newBuilder().setValue(number.getValue() * number.getValue()).build();
    }
}
