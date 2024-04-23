package com.zyneonstudios.application;

import com.formdev.flatlaf.FlatDarkLaf;
import com.zyneonstudios.application.frame.ApplicationFrame;

import javax.swing.*;

public class NexusApplication {

    public static String getApplicationPath() {
        return "A:/Sync/OneDrive/Projekte/Code/Zyneon-Application/application-main/target/run/";
    }

    private final ApplicationFrame frame;

    public NexusApplication(String[] args) {
        try {
            FlatDarkLaf.setup();
            UIManager.setLookAndFeel(new FlatDarkLaf());
        } catch (Exception ignore) {}
        frame = new ApplicationFrame("A:/Sync/OneDrive/Projekte/Code/Zyneon-Application/application-ui/content/start.html",getApplicationPath()+"libs/jcef/");
        frame.setVisible(true);
    }
}