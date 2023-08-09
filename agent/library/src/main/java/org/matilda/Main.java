package org.matilda;

import org.matilda.logger.StderrLogger;

public class Main {
    public static void main(String[] args) {
        pingToLoader();
        new MatildaAgent(new MatildaConnection(System.in, System.out), new StderrLogger()).run();
    }

    private static void pingToLoader() {
        System.out.write(0);
        System.out.flush();
    }
}