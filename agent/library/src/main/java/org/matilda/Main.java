package org.matilda;

public class Main {
    public static void main(String[] args) {
        pingToLoader();
        new MatildaAgent(new MatildaConnection(System.in, System.out)).run();
    }

    private static void pingToLoader() {
        System.out.write(0);
        System.out.flush();
    }
}