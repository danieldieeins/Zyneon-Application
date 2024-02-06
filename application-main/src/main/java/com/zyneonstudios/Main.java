package com.zyneonstudios;

import com.zyneonstudios.application.Application;
import com.zyneonstudios.application.frontend.ZyneonSplash;
import live.nerotv.shademebaby.logger.Logger;
import live.nerotv.shademebaby.utils.FileUtil;
import live.nerotv.shademebaby.utils.StringUtil;

import java.io.File;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Main {

    private static String applicationPath;
    private static Logger logger;
    public static String version;
    public static ZyneonSplash splash;
    public static String os;

    public static void main(String[] args) {
        splash = new ZyneonSplash();
        splash.setVisible(true);
        logger = new Logger("ZyneonApplication");
        version = "2024.2-beta.7";
        if(!new File(getDirectoryPath()+"libs/zyneon/"+ version +"/start.html").exists()) {
            FileUtil.deleteFolder(new File(getDirectoryPath()+"libs/zyneon/"));
            getLogger().debug("Deleted old UI Files: "+new File(getDirectoryPath()+"libs/zyneon/").mkdirs());
            FileUtil.downloadFile("https://github.com/danieldieeins/ZyneonApplicationContent/raw/main/h/" + version + "/content.zip", getDirectoryPath() + "libs/zyneon/" + version + ".zip");
            FileUtil.unzipFile(getDirectoryPath() + "libs/zyneon/" + version + ".zip", getDirectoryPath() + "libs/zyneon/" + version);
            getLogger().debug("Deleted UI ZIP File: "+new File(getDirectoryPath() + "libs/zyneon/" + version + ".zip").delete());
        }
        getLogger().debug("Deleted old updater json: "+new File(getDirectoryPath()+"updater.json").delete());
        getLogger().debug("Deleted old version json: "+new File(getDirectoryPath()+"version.json").delete());
        FileUtil.deleteFolder(new File(getDirectoryPath()+"temp/"));
        Application application = new Application(version+" ▪ Webium");
        if(args.length!=0) {
            if(args[0].startsWith("--test")) {
                String random = StringUtil.generateAlphanumericString(2)+"-"+StringUtil.generateAlphanumericString(3)+"-"+StringUtil.generateAlphanumericString(1);
                String date = new SimpleDateFormat("yyyyMMdd-HHmmss").format(Calendar.getInstance().getTime());
                application = new Application(date+" ▪ "+random);
            }
        }
        System.gc();
        application.start();
    }

    public static Logger getLogger() {
        return logger;
    }

    public static String getDirectoryPath() {
        if (applicationPath == null) {
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
                throw new RuntimeException(e.getMessage());
            }
            applicationPath = folderPath + "/";
        }
        return URLDecoder.decode(applicationPath, StandardCharsets.UTF_8);
    }
}