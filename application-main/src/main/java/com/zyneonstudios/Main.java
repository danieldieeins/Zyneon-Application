package com.zyneonstudios;

import com.zyneonstudios.application.NexusApplication;

public class Main {

    private static String[] args;

    public static void main(String[] args) {
        Main.args = args;
        new NexusApplication(args);
    }

    public static String[] getArgs() {
        return args;
    }
}