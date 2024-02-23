package com.zyneonstudios;

import com.zyneonstudios.application.Application;
import com.zyneonstudios.application.backend.installer.java.Architecture;
import com.zyneonstudios.application.backend.installer.java.OperatingSystem;
import com.zyneonstudios.application.backend.utils.ZLogger;
import com.zyneonstudios.application.backend.utils.frame.ZyneonSplash;
import live.nerotv.shademebaby.ShadeMeBaby;
import live.nerotv.shademebaby.utils.FileUtil;
import live.nerotv.shademebaby.utils.StringUtil;

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

    public static void main(String[] args) {
        version = "2024.2.6";
        String name = "Sunrise";
        architecture = getArchitecture();
        splash = new ZyneonSplash();
        splash.setVisible(true);
        logger = new ZLogger("ZYNEON");
        String fullVersion = version+" ▪ "+name;
        logger.log("[MAIN] Updated user interface: "+update());
        FileUtil.deleteFolder(new File(getDirectoryPath()+"temp/"));
        Application application = new Application(fullVersion);
        if(args.length!=0) {
            if(args[0].startsWith("--test")) {
                String random = StringUtil.generateAlphanumericString(2)+"-"+StringUtil.generateAlphanumericString(3)+"-"+StringUtil.generateAlphanumericString(1);
                String date = new SimpleDateFormat("yyyyMMdd-HHmmss").format(Calendar.getInstance().getTime());
                fullVersion = date+" ▪ "+random;
                application = new Application(fullVersion);
                logger.setDebugEnabled(true);
                ShadeMeBaby.getLogger().setDebugEnabled(true);
            }
        }
        System.gc();
        logger.log("[MAIN] Launching Zyneon Application version "+fullVersion+"...");
        application.start();
    }

    public static ZLogger getLogger() {
        return logger;
    }

    private static boolean update() {
        boolean updated;
        try {
            if (!new File(getDirectoryPath() + "libs/zyneon/" + version + "/start.html").exists()) {
                FileUtil.deleteFolder(new File(getDirectoryPath() + "libs/zyneon/"));
                logger.log("[MAIN] Deleted old user interface files: " + new File(getDirectoryPath() + "libs/zyneon/").mkdirs());
                FileUtil.downloadFile("https://github.com/danieldieeins/ZyneonApplicationContent/raw/main/h/" + version + "/content.zip", getDirectoryPath() + "libs/zyneon/" + version + ".zip");
                FileUtil.unzipFile(getDirectoryPath() + "libs/zyneon/" + version + ".zip", getDirectoryPath() + "libs/zyneon/" + version);
                logger.log("[MAIN] Deleted user interface archive: " + new File(getDirectoryPath() + "libs/zyneon/" + version + ".zip").delete());
                updated = true;
            } else {
                updated = false;
            }
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
}