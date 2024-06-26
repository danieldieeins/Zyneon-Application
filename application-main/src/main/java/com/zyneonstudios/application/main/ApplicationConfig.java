package com.zyneonstudios.application.main;

import com.zyneonstudios.Main;
import live.nerotv.shademebaby.file.Config;
import live.nerotv.shademebaby.utils.FileUtil;

import java.io.File;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Locale;
import java.util.UUID;

public record ApplicationConfig(String[] args) {

    public static String language = "en";
    public static String urlBase = "file://" + getApplicationPath() + "temp/ui/";
    public static String startPage = "discover.html";
    public static String theme = "automatic";
    public static boolean test = false;
    private static UUID applicationId = UUID.randomUUID();
    private static String applicationVersion = "unknown";
    private static String applicationPath = null;
    private static Config configuration = null;
    private static String os = null;
    private static Config updateConfig = null;
    private static Config properties = null;
    private static String[] arguments = null;

    public ApplicationConfig(String[] args) {
        this.args = args;
        arguments = this.args;
        for (String arg : args) {
            if (arg.startsWith("--ui:")) {
                urlBase = arg.replace("--ui:", "");
            }
            else if(arg.startsWith("--path:")) {
                applicationPath = arg.replace("--path:", "");
            }
            else if(arg.startsWith("--test")) {
                test = true;
            }
        }

        if(new File(getApplicationPath() + "temp/").exists()) {
            try {
                FileUtil.deleteFolder(new File(getApplicationPath() + "temp/"));
            } catch (Exception e) {
                NexusApplication.getLogger().error("[CONFIG] Couldn't delete old temp files: "+e.getMessage());
            }
        }
        if(new File(getApplicationPath() + "temp/nexus.json").exists()) {
            NexusApplication.getLogger().debug("[CONFIG] Deleted old properties: "+new File(getApplicationPath() + "temp/nexus.json").delete());
        }
        FileUtil.extractResourceFile("nexus.json",getApplicationPath()+"temp/nexus.json", Main.class);
        properties = new Config(new File(getApplicationPath() + "temp/nexus.json"));
        applicationVersion = properties.getString("version");

        String lang = Locale.getDefault().toLanguageTag();
        if(lang.startsWith("de-")) {
            getSettings().checkEntry("settings.language","de");
        } else {
            getSettings().checkEntry("settings.language","en");
        }

        getSettings().checkEntry("settings.applicationId", applicationId);
        applicationId = UUID.fromString(getSettings().getString("settings.applicationId"));

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

    public static String getApplicationVersion() {
        return applicationVersion;
    }

    public static UUID getApplicationId() {
        return applicationId;
    }
}
