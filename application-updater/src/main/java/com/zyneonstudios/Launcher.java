package com.zyneonstudios;

import com.zyneonstudios.application.ApplicationLauncher;
import com.zyneonstudios.application.ZyneonSplash;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Launcher {

    private static String applicationPath = null;
    private static ZyneonSplash splash = new ZyneonSplash();

    public static void main(String[] args) {
        splash.setVisible(true);
        System.out.println(new ApplicationLauncher(args).launch());
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
}