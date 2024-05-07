package com.zyneonstudios;

import com.zyneonstudios.application.main.ApplicationConfig;
import com.zyneonstudios.application.main.NexusApplication;

public class Main {

    /*
    * Zyneon Application entry point
    * by nerotvlive
    * Contributions are welcome. Please add your name to the "by" line if you make any modifications.
    */

    public static void main(String[] args) {
        // Creating application config
        new ApplicationConfig(args);
        // Launch new application object
        new NexusApplication().launch();
    }
}