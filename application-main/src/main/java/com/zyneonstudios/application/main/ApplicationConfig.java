package com.zyneonstudios.application.main;

import live.nerotv.shademebaby.file.Config;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Locale;

public record ApplicationConfig(String[] args) {

    /*
     * Zyneon Application config
     * by nerotvlive
     * Contributions are welcome. Please add your name to the "by" line if you make any modifications.
     * */

    public static String language = "en";
    public static String urlBase = "file://" + getApplicationPath() + "temp/ui/";
    public static String startPage = "discover.html";
    public static String theme = "automatic";
    public static boolean test = false;

    private static String applicationPath = null;
    private static Config configuration = null;
    private static String os = null;
    private static Config updateConfig = null;

    public static String[] arguments = null;

    // Constructor for ApplicationConfig class
    public ApplicationConfig(String[] args) {
        this.args = args;
        this.arguments = this.args;
        // Iterating through the command-line arguments
        for (String arg : args) {
            // Checking if the argument starts with "--ui:"
            if (arg.startsWith("--ui:")) {
                // If the argument starts with "--ui:", update the urlBase by replacing "--ui:" with an empty string
                // This extracts the URL specified after "--ui:" and sets it as the base URL for the application
                urlBase = arg.replace("--ui:", "");
            }
            // Checking if the argument starts with "--path:"
            else if(arg.startsWith("--path:")) {
                // If the argument starts with "--path:", update the applicationPath by replacing "--path:" with an empty string
                // This extracts the path specified after "--path:" and sets it as the application path
                applicationPath = arg.replace("--path:", "");
            }
            // Checking if the argument starts with "--test:"
            else if(arg.startsWith("--test")) {
                // If the argument starts with "--test", the test mode will be enabled
                test = true;
            }
        }
        String lang = Locale.getDefault().toLanguageTag();
        if(lang.startsWith("de-")) {
            getSettings().checkEntry("settings.language","de");
        } else {
            getSettings().checkEntry("settings.language","en");
        }
        if(getSettings().get("settings.startPage")!=null) {
            startPage = getSettings().getString("settings.startPage");
        }
        if(getSettings().get("settings.language")!=null) {
            language = getSettings().getString("settings.language");
        }
        if(getSettings().get("settings.theme")!=null) {
            theme = getSettings().getString("settings.theme");
        }
    }

    public static String[] getArguments() {
        return arguments;
    }

    // Method to get the application path as a string
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

    // Method to get the architecture of the operating system (String)
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

    // Method to get the operating system (string)
    public static String getOS() {
        return os;
    }

    // Method to get the application settings file
    public static Config getSettings() {
        if(configuration==null) {
            configuration = new Config(getApplicationPath()+"config/settings.json");
        }
        return configuration;
    }

    // Method to get the updater settings file
    public static Config getUpdateSettings() {
        if(updateConfig==null) {
            updateConfig = new Config(ApplicationConfig.getApplicationPath().replace("\\\\","\\").replace("\\","/").replace("/experimental/","/")+"libs/zyneon/updater.json");
        }
        return updateConfig;
    }
}
