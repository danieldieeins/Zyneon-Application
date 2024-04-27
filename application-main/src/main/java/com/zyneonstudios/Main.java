package com.zyneonstudios;

import com.zyneonstudios.application.Application;
import com.zyneonstudios.application.installer.java.Architecture;
import com.zyneonstudios.application.installer.java.OperatingSystem;
import com.zyneonstudios.application.utils.ZLogger;
import com.zyneonstudios.application.utils.frame.ZyneonSplash;
import live.nerotv.shademebaby.ShadeMeBaby;
import live.nerotv.shademebaby.utils.FileUtil;

import java.io.File;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class Main {

    private static String applicationPath;
    public static ZyneonSplash splash;
    private static ZLogger logger;
    public static String version;
    public static OperatingSystem operatingSystem;
    public static Architecture architecture;
    private static boolean test = false;

    public static void main(String[] args) {
        FileUtil.deleteFolder(new File(getDirectoryPath()+"temp/"));
        version = "2024.4.7";
        ArrayList<String> arguments = new ArrayList<>();
        String name = "";
        architecture = getArchitecture();
        splash = new ZyneonSplash();
        splash.setVisible(true);
        logger = new ZLogger("ZYNEON");
        String fullVersion = version+" ▪ "+name;
        logger.log("[MAIN] Updated user interface: "+update());
        boolean online = false;
        for(String arg:args) {
            arg = arg.toLowerCase();
            switch (arg) {
                case "--test" -> {
                    test = true;
                    fullVersion = version + " ▪ " + new SimpleDateFormat("yyyyMMdd-HHmmss").format(Calendar.getInstance().getTime());
                }
                case "--debug" -> {
                    logger.setDebugEnabled(true);
                    ShadeMeBaby.getLogger().setDebugEnabled(true);
                }
                case "--online" -> online = true;
            }
        }
        arguments.add(fullVersion);
        System.gc();
        logger.log("[MAIN] Launching Zyneon Application version "+fullVersion+"...");
        Application application = new Application(arguments);
        application.start(online);
    }

    public static ZLogger getLogger() {
        return logger;
    }

    private static boolean update() {
        boolean updated;
        try {
            new File(getDirectoryPath() + "libs/zyneon/instances.json").delete();
            logger.log("[MAIN] Deleted old user interface files: " + new File(getDirectoryPath() + "libs/zyneon/").mkdirs());
            logger.log("[MAIN] Created new user interface extraction folder: " + new File(getDirectoryPath() + "temp/ui/").mkdirs());
            FileUtil.extractResourceFile("ui.zip",getDirectoryPath()+"temp/ui.zip",Main.class);
            FileUtil.unzipFile(getDirectoryPath()+"temp/ui.zip", getDirectoryPath() + "temp/ui");
            logger.log("[MAIN] Deleted user interface archive: " + new File(getDirectoryPath()+"temp/ui.zip").delete());
            updated = true;
        } catch (Exception e) {
            logger.error("[MAIN] Couldn't update application user interface: "+e.getMessage());
            updated = false;
        }
        logger.log("[MAIN] Deleted old updater json: " + new File(getDirectoryPath() + "updater.json").delete());
        logger.log("[MAIN] Deleted old version json: " + new File(getDirectoryPath() + "version.json").delete());
        return updated;
    }

    public static String getDirectoryPath() {
        if (applicationPath == null) {
            String folderName = "Zyneon/Application";
            String appData;
            String os = System.getProperty("os.name").toLowerCase();
            if (os.contains("win")) {
                operatingSystem = OperatingSystem.Windows;
                appData = System.getenv("LOCALAPPDATA");
            } else if (os.contains("mac")) {
                operatingSystem = OperatingSystem.macOS;
                appData = System.getProperty("user.home") + "/Library/Application Support";
            } else {
                operatingSystem = OperatingSystem.Linux;
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

    private static Architecture getArchitecture() {
        String os = System.getProperty("os.arch");
        ArrayList<String> aarch = new ArrayList<>();
        aarch.add("ARM");
        aarch.add("ARM64");
        aarch.add("aarch64");
        aarch.add("armv6l");
        aarch.add("armv7l");
        for(String arch_os:aarch) {
            if(arch_os.equalsIgnoreCase(os)) {
                return Architecture.aarch64;
            }
        }
        return Architecture.x64;
    }

    public static boolean isTest() {
        return test;
    }
}