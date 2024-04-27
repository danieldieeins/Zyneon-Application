package com.zyneonstudios;

import com.zyneonstudios.application.ApplicationLauncher;
import com.zyneonstudios.application.ZyneonSplash;
import live.nerotv.shademebaby.logger.Logger;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Launcher {

    private static String applicationPath = null;
    private static ZyneonSplash splash = new ZyneonSplash();
    private static Logger logger = new Logger("ZYNEON");

    public static void main(String[] args) {
        splash.setVisible(true);
        logger.log("[LAUNCHER] Preparing launch...");
        ApplicationLauncher application = new ApplicationLauncher(args);
        if(application.automaticUpdates()) {
            application.update(application.getUpdateChannel(),false);
        }
        logger.log("[LAUNCHER] Launch application...");
        int i = application.launch(application.getUpdateChannel());
        logger.log("[LAUNCHER] Application launch process endet: "+i);
        System.exit(i);
    }

    public static String getDirectoryPath() {
        if (applicationPath == null) {
            String folderName = "Zyneon/Application";
            String appData;
            String os = System.getProperty("os.name").toLowerCase();
            if (os.contains("win")) {
                appData = System.getenv("LOCALAPPDATA");
            } else if (os.contains("mac")) {
                appData = System.getProperty("user.home") + "/Library/Application Support";
            } else {
                appData = System.getProperty("user.home") + "/.local/share";
            }
            Path folderPath = Paths.get(appData, folderName);
            try {
                Files.createDirectories(folderPath);
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage());
            }
            applicationPath = folderPath + "/";
        }
        return URLDecoder.decode(applicationPath, StandardCharsets.UTF_8);
    }

    public static ZyneonSplash getSplash() {
        return splash;
    }

    public static Logger getLogger() {
        return logger;
    }
}