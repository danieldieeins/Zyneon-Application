package live.nerotv;

import live.nerotv.zyneon.app.application.Application;
import live.nerotv.zyneon.app.application.backend.utils.Config;

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
    private static boolean sendDebug = false;

    public static void main(String[] args) {
        config = new Config(new File(getDirectoryPath()+"config.json"));
        if(config.get("settings.memory.default")==null) {
            config.set("settings.memory.default",2048);
        }
        new Application().start();
    }

    public static boolean isDebugEnabled() {
        return sendDebug;
    }

    public static String getDirectoryPath() {
        if(path == null) {
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
                e.printStackTrace();
            }
            path = folderPath+"/";
        }
        return URLDecoder.decode(path, StandardCharsets.UTF_8);
    }

    public static String getZyversePath() {
        if(zyverse == null) {
            String folderName = "Zyneon/Zyverse";
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
                e.printStackTrace();
            }
            zyverse = folderPath+"/";
        }
        return URLDecoder.decode(zyverse, StandardCharsets.UTF_8);
    }

    public static void debug(String message) {
        if(sendDebug) {
            System.out.println("[DEBUG] " + message);
        }
    }
}