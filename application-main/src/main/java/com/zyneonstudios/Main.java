package com.zyneonstudios;

import com.zyneonstudios.application.frame.ZyneonSplash;
import com.zyneonstudios.application.main.ApplicationConfig;
import com.zyneonstudios.application.main.NexusApplication;

public class Main {

    public static ZyneonSplash splash = null;

    /*
    * Zyneon Application entry point
    * by nerotvlive
    * Contributions are welcome. Please add your name to the "by" line if you make any modifications.
    */

    public static void main(String[] args) {
        // Creating new splash icon
        splash = new ZyneonSplash();
        // Showing splash icon
        splash.setVisible(true);
        // Creating application config
        new ApplicationConfig(args);
        // Launch new application object
        new NexusApplication().launch();
    }
}