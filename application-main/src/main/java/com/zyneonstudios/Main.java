package com.zyneonstudios;

import com.zyneonstudios.application.main.ApplicationConfig;
import com.zyneonstudios.application.main.NexusApplication;

public class Main {

    public static void main(String[] args) {
        ApplicationConfig config = new ApplicationConfig(args);
        new NexusApplication(config).launch();
    }
}