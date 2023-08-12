package org.matilda.commands;

import com.google.protobuf.Int32Value;

@MatildaService
public class MathService {
    @MatildaCommand
    public Int32Value square(int number) {
        return Int32Value.newBuilder().setValue(number * number).build();
    }

    @MatildaCommand
    public Int32Value sum(int first, int second) {
        return Int32Value.newBuilder().setValue(first + second).build();
    }
}
