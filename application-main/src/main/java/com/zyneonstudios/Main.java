package com.zyneonstudios;

import com.zyneonstudios.application.NexusApplication;
import live.nerotv.shademebaby.file.Config;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class Main {

    private static String applicationPath = null;
    private static Config config;
    private static NexusApplication application;
    private static String os;

    private static String[] args;

    public static void main(String[] args) {
        Main.args = args;
        for(String arg:args) {
            if(arg.startsWith("--path:")) {
                applicationPath = arg.replace("--path:", "");
            }
        }
        config = new Config(getApplicationPath()+"config/settings.json");
        getApplication().launch();
    }

    public static NexusApplication getApplication() {
        if(application==null) {
            application = new NexusApplication(args);
        }
        return application;
    }

    public static Config getConfig() {
        return config;
    }

    public static String getApplicationPath() {
        if (applicationPath == null) {
            String folderName = "Zyneon/Application";
            String appData;
            String os = System.getProperty("os.name").toLowerCase();
            if (os.contains("win")) {
                appData = System.getenv("LOCALAPPDATA");
                Main.os = "Windows-"+getArchitecture();
            } else if (os.contains("mac")) {
                appData = System.getProperty("user.home") + "/Library/Application Support";
                Main.os = "macOS-"+getArchitecture();
            } else {
                appData = System.getProperty("user.home") + "/.local/share";
                Main.os = System.getProperty("os.name")+"-"+getArchitecture();
            }
            Path folderPath = Paths.get(appData, folderName);
            try {
                Files.createDirectories(folderPath);
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage());
            }
            applicationPath = folderPath + "/";
            return URLDecoder.decode(applicationPath, StandardCharsets.UTF_8);
        } else if(os == null) {
            String os = System.getProperty("os.name").toLowerCase();
            if (os.contains("win")) {
                Main.os = "Windows-"+getArchitecture();
            } else if (os.contains("mac")) {
                Main.os = "macOS-"+getArchitecture();
            } else {
                Main.os = System.getProperty("os.name")+"-"+getArchitecture();
            }
        }
        return applicationPath;
    }

    private static String getArchitecture() {
        String os = System.getProperty("os.arch");
        ArrayList<String> aarch = new ArrayList<>();
        aarch.add("ARM");
        aarch.add("ARM64");
        aarch.add("aarch64");
        aarch.add("armv6l");
        aarch.add("armv7l");
        for(String arch_os:aarch) {
            if(arch_os.equalsIgnoreCase(os)) {
                return "aarch64";
            }
        }
        return "x64";
    }

    public static String getOS() {
        return os;
    }
}