package com.zyneonstudios;

import com.zyneonstudios.application.frame.ZyneonSplash;
import com.zyneonstudios.application.main.NexusApplication;
import com.zyneonstudios.nexus.desktop.NexusDesktop;
import java.net.MalformedURLException;

public class Main {

    public static ZyneonSplash splash = null;

    public static void main(String[] args) throws MalformedURLException {
        NexusDesktop.init();
        splash = new ZyneonSplash();
        splash.setVisible(true);
        NexusApplication application = new NexusApplication(args);
        application.launch();
    }
}