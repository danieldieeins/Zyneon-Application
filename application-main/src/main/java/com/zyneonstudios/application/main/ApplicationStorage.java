package com.zyneonstudios.application.main;

import com.zyneonstudios.Main;
import com.zyneonstudios.application.frame.web.ApplicationFrame;
import live.nerotv.shademebaby.file.Config;
import live.nerotv.shademebaby.utils.FileUtil;
import live.nerotv.shademebaby.utils.StringUtil;

import java.io.File;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Locale;
import java.util.UUID;

@SuppressWarnings("unused")
public record ApplicationStorage(String[] args, NexusApplication app) {

    public static String language = "en";
    public static String urlBase = "file://" + getApplicationPath() + "temp/ui/";
    public static String startPage = "discover.html";
    public static String theme = "automatic";
    public static boolean test = false;
    private static UUID applicationId = UUID.randomUUID();
    private static String applicationVersion = StringUtil.generateAlphanumericString(6)+"-"+StringUtil.generateAlphanumericString(3);
    private static String applicationName = "Unofficial NEXUS App";
    private static String applicationPath = null;
    private static Config configuration = null;
    private static String os = null;
    private static Config updateConfig = null;
    private static String[] arguments = null;
    private static boolean driveAccess = false;
    private static NexusApplication application = null;

    public ApplicationStorage(String[] args, NexusApplication app) {
        this.app = app;
        application = app;
        this.args = args;
        arguments = this.args;

        for (String arg : args) {
            if(arg.startsWith("--test")) {
                test = true;
                NexusApplication.getLogger().setDebugEnabled(true);
            } else if (arg.startsWith("--ui:")) {
                urlBase = arg.replace("--ui:", "");
            }
            else if(arg.startsWith("--path:")) {
                applicationPath = arg.replace("--path:", "");
            }
        }

        FileUtil.extractResourceFile("nexus.json",getApplicationPath()+"temp/nexus.json", Main.class);
        Config properties = new Config(new File(getApplicationPath() + "temp/nexus.json"));

            if (properties.getString("version") != null) {
                applicationVersion = properties.getString("version");
            }
            if (properties.getString("name") != null) {
                applicationName = properties.getString("name");
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

    public static boolean hasDriveAccess() {
        return driveAccess;
    }

    public static void enableDriveAccess() {
        ((ApplicationFrame)application.getFrame()).executeJavaScript("document.getElementById('drive-button').style.display = 'flex';");
        driveAccess = true;
    }

    public static void disableDriveAccess() {
        driveAccess = false;
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
                ApplicationStorage.os = "Windows-"+getArchitecture();
            } else if (os.contains("mac")) {
                appData = System.getProperty("user.home") + "/Library/Application Support";
                ApplicationStorage.os = "macOS-"+getArchitecture();
            } else {
                appData = System.getProperty("user.home") + "/.local/share";
                ApplicationStorage.os = System.getProperty("os.name")+"-"+getArchitecture();
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
                ApplicationStorage.os = "Windows-"+getArchitecture();
            } else if (os.contains("mac")) {
                ApplicationStorage.os = "macOS-"+getArchitecture();
            } else {
                ApplicationStorage.os = System.getProperty("os.name")+"-"+getArchitecture();
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
            updateConfig = new Config(ApplicationStorage.getApplicationPath().replace("\\\\","\\").replace("\\","/").replace("/experimental/","/")+"libs/zyneon/updater.json");
        }
        return updateConfig;
    }

    public static String getApplicationVersion() {
        return applicationVersion;
    }

    public static String getApplicationName() {
        return applicationName;
    }

    public static UUID getApplicationId() {
        return applicationId;
    }

    public static boolean isOffline() {
        try {
            URL url = new URL("https://zyneonstudios.github.io/nexus-nex/");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("HEAD");
            int responseCode = connection.getResponseCode();
            return !(responseCode == HttpURLConnection.HTTP_OK);
        } catch (Exception e) {
            return true;
        }
    }
}