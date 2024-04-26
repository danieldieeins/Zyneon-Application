package com.zyneonstudios.application;

import com.formdev.flatlaf.FlatDarkLaf;
import com.zyneonstudios.Main;
import com.zyneonstudios.application.frame.ApplicationFrame;

import javax.swing.*;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

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

    private static String loadResourceAsString(String resourceName) {
        try {
            URL resourceUrl = Main.class.getResource(resourceName);
            if (resourceUrl == null) {
                throw new IOException("Resource not found: " + resourceName);
            }
            InputStream inputStream = resourceUrl.openStream();
            byte[] buffer = new byte[inputStream.available()];
            inputStream.read(buffer);
            inputStream.close();
            return new String(buffer);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}