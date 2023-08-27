package live.nerotv;

import live.nerotv.zyneon.app.application.Application;
import live.nerotv.zyneon.app.application.backend.modpack.creator.ModpackCreator;
import live.nerotv.zyneon.app.application.backend.utils.Config;
import live.nerotv.zyneon.app.updater.Updater;
import java.io.File;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {

    private static String[] arguments;
    private static String path;
    public static Config config;
    private static boolean sendDebug = false;

    public static void main(String[] args) {
        config = new Config(new File(getDirectoryPath()+"config.json"));
        if(config.get("settings.memory.default")==null) {
            config.set("settings.memory.default",2048);
        }
        arguments = args;
        if(arguments.length > 0) {
            if(arguments.length > 1) {
                if(arguments[1].equalsIgnoreCase("debug")) {
                    sendDebug = true;
                }
            }
            if(arguments[0].equalsIgnoreCase("creator")) {
                new ModpackCreator().start();
            } else if(arguments[0].equalsIgnoreCase("updater")) {
                new Updater();
            } else if(arguments[0].equalsIgnoreCase("application")) {
                new Application().start();
            } else {
                System.out.println("invalid arguments");
                System.exit(-1);
            }
            return;
        }
        String jarPath = Main.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        if (jarPath.startsWith("/")) {
            jarPath = jarPath.substring(1);
        }
        try {
            jarPath = java.net.URLDecoder.decode(jarPath, "UTF-8");
        } catch (java.io.UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if(jarPath.contains("app.jar")) {
            new Application().start();
            return;
        }
        new Updater();
    }

    public static String[] getArguments() {
        return arguments;
    }

    public static String getDirectoryPath() {
        if(path == null) {
            String folderName = "ZyneonApplication";
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
        return URLDecoder.decode(path,StandardCharsets.UTF_8);
    }

    public static void debug(String message) {
        if(sendDebug) {
            System.out.println("[DEBUG] " + message);
        }
    }
}