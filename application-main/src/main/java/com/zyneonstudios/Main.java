package com.zyneonstudios;

import com.zyneonstudios.application.frame.ZyneonSplash;
import com.zyneonstudios.application.main.ApplicationConfig;
import com.zyneonstudios.application.main.NexusApplication;

public class Main {

    public static ZyneonSplash splash = null;

    public static void main(String[] args) {
        splash = new ZyneonSplash();
        splash.setVisible(true);
        new ApplicationConfig(args);
        new NexusApplication().launch();
    }
}