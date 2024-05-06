package com.zyneonstudios.application.main;

import live.nerotv.shademebaby.file.Config;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public record ApplicationConfig(String[] args) {

    public static String urlBase = "file://" + getApplicationPath() + "temp/ui/";

    private static String applicationPath = null;
    private static Config configuration = null;
    private static String os = null;
    private static Config updateConfig = null;

    public ApplicationConfig(String[] args) {
        this.args = args;
        for (String arg : args) {
            if (arg.startsWith("--ui:")) {
                urlBase = arg.replace("--ui:", "");
            } else if(arg.startsWith("--path:")) {
                applicationPath = arg.replace("--path:", "");
            }
        }
    }

    public static String getApplicationPath() {
        if (applicationPath == null) {
            String folderName = "Zyneon/Application/experimental";
            String appData;
            String os = System.getProperty("os.name").toLowerCase();
            if (os.contains("win")) {
                appData = System.getenv("LOCALAPPDATA");
                ApplicationConfig.os = "Windows-"+getArchitecture();
            } else if (os.contains("mac")) {
                appData = System.getProperty("user.home") + "/Library/Application Support";
                ApplicationConfig.os = "macOS-"+getArchitecture();
            } else {
                appData = System.getProperty("user.home") + "/.local/share";
                ApplicationConfig.os = System.getProperty("os.name")+"-"+getArchitecture();
            }
            Path folderPath = Paths.get(appData, folderName);
            try {
                Files.createDirectories(folderPath);
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage());
            }
            applicationPath = folderPath + "/";
        } else if(os == null) {
            String os = System.getProperty("os.name").toLowerCase();
            if (os.contains("win")) {
                ApplicationConfig.os = "Windows-"+getArchitecture();
            } else if (os.contains("mac")) {
                ApplicationConfig.os = "macOS-"+getArchitecture();
            } else {
                ApplicationConfig.os = System.getProperty("os.name")+"-"+getArchitecture();
            }
        }
        return URLDecoder.decode(applicationPath, StandardCharsets.UTF_8);
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

    public static Config getSettings() {
        if(configuration==null) {
            configuration = new Config(getApplicationPath()+"config/settings.json");
        }
        return configuration;
    }

    public static Config getUpdateSettings() {
        if(updateConfig==null) {
            updateConfig = new Config(ApplicationConfig.getApplicationPath().replace("\\\\","\\").replace("\\","/").replace("/experimental/","/")+"libs/zyneon/updater.json");
        }
        return updateConfig;
    }
}