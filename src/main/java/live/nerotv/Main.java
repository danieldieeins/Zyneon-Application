package live.nerotv;

import live.nerotv.shademebaby.ShadeMeBaby;
import live.nerotv.shademebaby.file.Config;
import live.nerotv.shademebaby.file.FileUtils;
import live.nerotv.shademebaby.logger.Logger;
import live.nerotv.zyneon.app.application.Application;
import live.nerotv.zyneon.app.application.frontend.ZyneonSplash;

import java.io.File;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {

    private static String zyverse;
    private static String path;
    public static Config config;
    private static Logger logger;
    public static String v;
    public static String language = "english";
    public static String starttab = "start";
    public static ZyneonSplash splash;

    public static void main(String[] args) {
        splash = new ZyneonSplash();
        splash.setVisible(true);
        v = "PB9.1";
        if(!new File(getDirectoryPath()+"libs/zyneon/"+v+"/index.html").exists()) {
            new File(getDirectoryPath()+"libs/zyneon/").mkdirs();
            FileUtils.downloadFile("https://github.com/danieldieeins/ZyneonApplicationContent/raw/main/h/" + v + "/content.zip", getDirectoryPath() + "libs/zyneon/" + v + ".zip");
            FileUtils.unzipFile(getDirectoryPath() + "libs/zyneon/" + v + ".zip", getDirectoryPath() + "libs/zyneon/" + v);
            new File(getDirectoryPath() + "libs/zyneon/" + v + ".zip").delete();
        }
        config = new Config(new File(getDirectoryPath() + "config.json"));
        config.checkEntry("settings.starttab","start");
        starttab = config.getString("settings.starttab");
        config.checkEntry("settings.language","auto");
        if(!config.getString("settings.language").equalsIgnoreCase("auto")) {
            language = config.getString("settings.language");
        } else {
            if (System.getProperty("user.language").equalsIgnoreCase("de")) {
                language = "german";
            }
        }
        config.checkEntry("settings.memory.default", 1024);
        config.checkEntry("settings.logger.debug", false);
        logger = new Logger("ZyneonApplication");
        logger.setDebugEnabled(config.getBool("settings.logger.debug"));
        ShadeMeBaby.getLogger().setDebugEnabled(config.getBool("settings.logger.debug"));
        new Application("1.0 Public Beta 9").start();
    }

    public static Logger getLogger() {
        return logger;
    }


    public static String getDirectoryPath() {
        if (path == null) {
            String folderName = "Zyneon/Application";
            String appData;
            String os = System.getProperty("os.name").toLowerCase();
            if (os.contains("win")) {
                Main.os = "Windows";
                appData = System.getenv("LOCALAPPDATA");
            } else if (os.contains("mac")) {
                Main.os = "macOS";
                appData = System.getProperty("user.home") + "/Library/Application Support";
            } else {
                Main.os = "Unix";
                appData = System.getProperty("user.home") + "/.local/share";
            }
            Path folderPath = Paths.get(appData, folderName);
            try {
                Files.createDirectories(folderPath);
            } catch (Exception e) {
                e.printStackTrace();
            }
            path = folderPath + "/";
        }
        return URLDecoder.decode(path, StandardCharsets.UTF_8);
    }

    public static String os;

    public static String getZyversePath() {
        if (zyverse == null) {
            String folderName = "Zyneon/Zyverse";
            String appData;
            String os = System.getProperty("os.name").toLowerCase();
            if (os.contains("win")) {
                Main.os = "Windows";
                appData = System.getenv("LOCALAPPDATA");
            } else if (os.contains("mac")) {
                Main.os = "macOS";
                appData = System.getProperty("user.home") + "/Library/Application Support";
            } else {
                Main.os = "Unix";
                appData = System.getProperty("user.home") + "/.local/share";
            }
            Path folderPath = Paths.get(appData, folderName);
            try {
                Files.createDirectories(folderPath);
            } catch (Exception e) {
                e.printStackTrace();
            }
            zyverse = folderPath + "/";
        }
        return URLDecoder.decode(zyverse, StandardCharsets.UTF_8);
    }
}