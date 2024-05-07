package com.zyneonstudios.application.main;

import com.formdev.flatlaf.FlatDarkLaf;
import com.zyneonstudios.Main;
import com.zyneonstudios.application.frame.web.ApplicationFrame;
import com.zyneonstudios.application.frame.web.CustomApplicationFrame;
import live.nerotv.shademebaby.logger.Logger;
import live.nerotv.shademebaby.utils.FileUtil;

import javax.swing.*;
import java.awt.*;
import java.io.File;

import static com.zyneonstudios.application.main.ApplicationConfig.getApplicationPath;

public class NexusApplication{

    private final JFrame frame;
    private static final Logger logger = new Logger("APP");

    public NexusApplication() {
        logger.log("[APP] Updated application ui: "+update());
        try {
            FlatDarkLaf.setup();
            UIManager.setLookAndFeel(new FlatDarkLaf());
        } catch (Exception ignore) {}
        if(ApplicationConfig.getOS().startsWith("macOS")||ApplicationConfig.getOS().startsWith("Windows")) {
            frame = new ApplicationFrame(ApplicationConfig.urlBase + "start.html", getApplicationPath() + "libs/jcef/");
            frame.pack(); frame.setSize(new Dimension(1200,720));
        } else {
            frame = new CustomApplicationFrame(ApplicationConfig.urlBase + "start.html", getApplicationPath() + "libs/jcef/");
            frame.pack();
        }
        frame.setLocationRelativeTo(null);
    }

    public static Logger getLogger() {
        return logger;
    }

    private static boolean update() {
        boolean updated;
        try {
            if(new File(getApplicationPath() + "temp/ui/").exists()) {
                logger.debug("[APP] Deleted old ui files: "+new File(getApplicationPath() + "temp/ui/").delete());
            }
            logger.debug("[APP] Created new ui path: "+new File(getApplicationPath() + "temp/ui/").mkdirs());
            FileUtil.extractResourceFile("ui.zip",getApplicationPath()+"temp/ui.zip",Main.class);
            FileUtil.unzipFile(getApplicationPath()+"temp/ui.zip", getApplicationPath() + "temp/ui");
            logger.debug("[APP] Deleted ui archive: "+new File(getApplicationPath()+"temp/ui.zip").delete());
            updated = true;
        } catch (Exception e) {
            logger.error("[APP] Couldn't update application user interface: "+e.getMessage());
            updated = false;
        }
        logger.debug("[APP] Deleted old updatar json: "+new File(getApplicationPath() + "updater.json").delete());
        logger.debug("[APP] Deleted older updater json: "+new File(getApplicationPath() + "version.json").delete());
        return updated;
    }

    public void launch() {
        frame.setVisible(true);
    }
}