package com.zyneonstudios.application.main;

import com.formdev.flatlaf.FlatDarkLaf;
import com.zyneonstudios.Main;
import com.zyneonstudios.application.frame.ApplicationFrame;
import live.nerotv.shademebaby.logger.Logger;
import live.nerotv.shademebaby.utils.FileUtil;

import javax.swing.*;
import java.io.File;

import static com.zyneonstudios.application.main.ApplicationConfig.getApplicationPath;

public class NexusApplication{

    private final ApplicationFrame frame;

    private static ApplicationConfig config = null;
    private static final Logger logger = new Logger("APP");

    public NexusApplication(ApplicationConfig config) {
        NexusApplication.config = config;
        update();
        try {
            FlatDarkLaf.setup();
            UIManager.setLookAndFeel(new FlatDarkLaf());
        } catch (Exception ignore) {}
        frame = new ApplicationFrame(ApplicationConfig.urlBase +"start.html", getApplicationPath()+"libs/jcef/");
    }

    public static ApplicationConfig getConfig() {
        return config;
    }

    public static Logger getLogger() {
        return logger;
    }

    private static boolean update() {
        boolean updated;
        try {
            if(new File(getApplicationPath() + "temp/ui/").exists()) {
                new File(getApplicationPath() + "temp/ui/").delete();
            }
            new File(getApplicationPath() + "temp/ui/").mkdirs();
            FileUtil.extractResourceFile("ui.zip",getApplicationPath()+"temp/ui.zip",Main.class);
            FileUtil.unzipFile(getApplicationPath()+"temp/ui.zip", getApplicationPath() + "temp/ui");
            new File(getApplicationPath()+"temp/ui.zip").delete();
            updated = true;
        } catch (Exception e) {
            System.err.println("Couldn't update application user interface: "+e.getMessage());
            updated = false;
        }
        new File(getApplicationPath() + "updater.json").delete();
        new File(getApplicationPath() + "version.json").delete();
        return updated;
    }

    public void launch() {
        frame.setVisible(true);
    }
}