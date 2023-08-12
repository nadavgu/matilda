package org.matilda.commands;

@MatildaService
public class MathService {
    @MatildaCommand
    public int square(int number) {
        return number * number;
    }

    @MatildaCommand
    public int sum(int first, int second) {
        return first + second;
    }
}
