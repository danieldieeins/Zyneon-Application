package com.zyneonstudios.nexus.application.bootstrapper;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.zyneonstudios.Main;
import com.zyneonstudios.application.frame.ZyneonSplash;
import com.zyneonstudios.application.main.NexusApplication;
import com.zyneonstudios.nexus.desktop.NexusDesktop;
import com.zyneonstudios.nexus.utilities.file.FileGetter;
import com.zyneonstudios.nexus.utilities.json.GsonUtility;
import com.zyneonstudios.nexus.utilities.storage.JsonStorage;
import org.apache.commons.io.FileUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;

public class ApplicationBootstrapper {

    private static File appFolder = null;
    private static String updateChannel = null;
    private static final ArrayList<String> arguments = new ArrayList<>();
    private static JsonStorage config = null;
    public static ApplicationSplash splash = new ApplicationSplash("/updater-logo.png");

    public static void main_(String[] args) {
        new UpdateWindow().setVisible(true);
    }

    public static void main(String[] args) {
        splash.setVisible(true);
        resolveArguments(args);
        if(appFolder == null) {
            appFolder = getDefaultFolder(false);
        }
        checkForOldFolder();
        if(!appFolder.exists()) {
            if(!appFolder.mkdirs()) {
                System.err.println("Couldn't create application folder.");
                System.exit(1);
            }
        }
        initConfig();
        if(update()) {
            String version = config.getString("updater.versions.app.value");
            String path = (appFolder.getAbsolutePath()+"/libraries/zyneon/versions/"+updateChannel.toLowerCase()+"/"+version+".jar").replace("\\","/");
            try {
                ProcessBuilder processBuilder = new ProcessBuilder("java", "-jar", "--add-opens", "java.desktop/sun.awt=ALL-UNNAMED", "--add-opens", "java.desktop/sun.lwawt=ALL-UNNAMED", "--add-opens", "java.desktop/sun.lwawt.macosx=ALL-UNNAMED", path, "application", "--add-opens java.desktop/sun.awt=ALL-UNNAMED --add-opens java.desktop/sun.lwawt=ALL-UNNAMED --add-opens java.desktop/sun.lwawt.macosx=ALL-UNNAMED","--path:"+appFolder.getAbsolutePath().replace("\\","/")+"/");
                processBuilder.redirectErrorStream(true);
                Process process = processBuilder.start();
                InputStream inputStream = process.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                splash.setVisible(false);
                while ((line = reader.readLine()) != null) {
                    System.out.println(line);
                }
                System.exit(process.waitFor());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else {
            splash.setVisible(false);
            launchFallback();
        }
    }

    private static void resolveArguments(String[] args) {
        int size = args.length;
        int self = 0;
        for(String arg:args) {
            arguments.add(arg);
            int next = self+1;
            if(arg.startsWith("--")) {
                arg = arg.replaceFirst("--","");
                if(arg.equals("path")) {
                    if(appFolder == null) {
                        if (next < size) {
                            try {
                                String path = args[next];
                                appFolder = new File(path);
                            } catch (Exception e) {
                                System.err.println("Invalid path specified. (" + e.getMessage() + ")");
                                System.exit(1);
                            }
                        } else {
                            System.err.println("No path specified.");
                            System.exit(1);
                        }
                    }
                } else if(arg.equals("channel")) {
                    if(next<size) {
                        updateChannel = args[next];
                        if(updateChannel.equalsIgnoreCase("old")) {
                            appFolder = getDefaultFolder(true);
                        }
                    } else {
                        System.err.println("No update channel specified.");
                        System.exit(1);
                    }
                }
            }
            self=next;
        }
    }

    private static File getDefaultFolder(boolean old) {
        String folderName = "Zyneon/NEXUS App";
        if(old) {
            folderName = "Zyneon/Application";
        }
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
        String applicationPath = folderPath.toString().replace("\\","/") + "/";
        return new File(applicationPath);
    }

    public static File getAppFolder() {
        return appFolder;
    }

    public static String getUpdateChannel() {
        return updateChannel;
    }

    private static void initConfig() {
        config = new JsonStorage(appFolder.getAbsolutePath()+"/config/updater.json");
        config.ensure("updater.versions.self","null");
        config.ensure("updater.versions.app.type","null");
        config.ensure("updater.versions.app.value","null");
        config.ensure("updater.settings.updateApp",true);
        if(updateChannel==null) {
            updateChannel = config.getString("updater.versions.app.type");
        }
    }

    private static void launchFallback() {
        arguments.add("--offline");
        String path = "--path:"+appFolder.getAbsolutePath().replace("\\","/");
        if(!path.endsWith("/")) {
            path = path+"/";
        }
        arguments.add(path);
        try {
            Main.splash = new ZyneonSplash();
            Main.splash.setVisible(true);
            NexusDesktop.getLogger().setName("APP",true);
            NexusDesktop.init();
            new NexusApplication(arguments.toArray(new String[0])).launch();
        } catch (Exception e) {
            System.out.println("Couldn't launch fallback version...");
            System.exit(1);
        }
    }

    private static final HashMap<String,JsonObject> updateChannels = new HashMap<>();
    private static boolean isUpToDate(boolean retry) {
        try {
            if(!config.getBoolean("updater.settings.updateApp")) {
                return true;
            }
            String meta = "https://zyneonstudios.github.io/nexus-nex/application/index.json";
            JsonObject json = new Gson().fromJson(GsonUtility.getFromURL(meta), JsonObject.class);
            if(json.has("versions")) {
                JsonArray versions = json.getAsJsonArray("versions");
                if(!versions.isEmpty()) {
                    for (int i = 0; i < versions.size(); i++) {
                        try {
                            JsonObject updateChannel = versions.get(i).getAsJsonObject();
                            if(updateChannel.has("meta")) {
                                JsonObject channelMeta = updateChannel.getAsJsonObject("meta");
                                if(channelMeta.has("id")) {
                                    String id = channelMeta.get("id").getAsString();
                                    updateChannels.put(id,updateChannel);
                                }
                            }
                        } catch (Exception ignore) {}
                    }
                }
            }
            if(!updateChannels.isEmpty()) {
                if(updateChannels.containsKey(updateChannel)) {
                    JsonObject channel = updateChannels.get(updateChannel);
                    if(channel.has("info")) {
                        JsonObject info = channel.getAsJsonObject("info");
                        JsonObject meta_ = channel.getAsJsonObject("meta");
                        if(info.has("version")) {
                            String version = info.get("version").getAsString();
                            if(meta_.has("download")) {
                                if(!config.getString("updater.versions.app.type").equals(updateChannel)||!config.getString("updater.versions.app.value").equals(version)) {
                                    System.out.println("Found update "+updateChannel+"-"+version+". Current version: "+config.getString("updater.versions.app.type")+"-"+config.getString("updater.versions.app.value"));
                                    return false;
                                }
                            }
                        } else {
                            if(!retry) {
                                updateChannel = "stable";
                                return isUpToDate(true);
                            } else {
                                System.err.println("Invalid update channel specified.");
                                System.exit(1);
                            }
                        }
                    }
                } else {
                    if(!retry) {
                        updateChannel = "stable";
                        return isUpToDate(true);
                    } else {
                        System.err.println("Invalid update channel specified.");
                        System.exit(1);
                    }
                }
            }
            throw new RuntimeException("Invalid meta format");
        } catch (Exception e) {
            return true;
        }
    }

    public static boolean update() {
        if(isUpToDate(false)) {
            String current_version = config.getString("updater.versions.app.value");
            String current_path = appFolder.getAbsolutePath().replace("\\","/")+"/libraries/zyneon/versions/"+updateChannel.toLowerCase()+"/"+current_version+".jar";
            if(new File(current_path).exists()) {
                return true;
            }
        }
        try {
            ApplicationSplash updateSplash = new ApplicationSplash("/updater-active.png");
            updateSplash.setVisible(true);
            splash.setVisible(false);
            splash = updateSplash;
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        JsonObject channel = updateChannels.get(updateChannel);
        JsonObject info = channel.getAsJsonObject("info");
        JsonObject meta = channel.getAsJsonObject("meta");
        String id = meta.get("id").getAsString();
        String version = info.get("version").getAsString();
        String download = meta.get("download").getAsString();
        String fileName = version+".jar";
        try {
            String folder = appFolder.getAbsolutePath().replace("\\","/")+"/libraries/zyneon/versions/"+id+"/";
            if(!new File(folder).exists()) {
                if(!new File(folder).mkdirs()) {
                    return false;
                }
            }
            String path = folder+fileName;
            FileGetter.downloadFile(download,path);
            if(new File(path).exists()) {
                config.set("updater.versions.app.type",updateChannel);
                config.set("updater.versions.app.value",version);
                return true;
            }
        } catch (Exception ignore) {}
        return false;
    }

    private static void checkForOldFolder() {
        try {
            Path source = Path.of(getDefaultFolder(true).getAbsolutePath() + "/experimental");
            Path target = Path.of(appFolder.getAbsolutePath());
            if (!target.toFile().exists()) {
                if (source.toFile().exists()) {
                    FileUtils.copyDirectory(source.toFile(), target.toFile());
                    try {
                        FileUtils.deleteDirectory(source.toFile());
                    } catch (Exception ignore) {}
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
