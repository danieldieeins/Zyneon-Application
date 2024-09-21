package com.zyneonstudios;

import com.zyneonstudios.application.frame.ZyneonSplash;
import com.zyneonstudios.application.main.NexusApplication;
import com.zyneonstudios.nexus.desktop.NexusDesktop;

public class Main {

    public static ZyneonSplash splash = null;

    public static void main(String[] args) {
        splash = new ZyneonSplash();
        splash.setVisible(true);
        NexusDesktop.getLogger().setName("APP",true);
        NexusDesktop.init();
        new NexusApplication(args).launch();
    }
}