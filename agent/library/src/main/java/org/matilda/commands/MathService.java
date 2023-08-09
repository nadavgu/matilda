package org.matilda.commands;

@MatildaService
public class MathService {
    @MatildaCommand
    public int square(int number) {
        return number * number;
    }
}
