package com.zyneonstudios.application;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.zyneonstudios.Launcher;
import live.nerotv.shademebaby.file.Config;
import live.nerotv.shademebaby.utils.FileUtil;
import live.nerotv.shademebaby.utils.GsonUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Objects;

public class ApplicationLauncher {

    private final String libraries = Launcher.getDirectoryPath()+"libs/zyneon/";
    private final Config config = new Config(libraries+"updater.json");
    private final boolean autoUpdate;
    private final String updateChannel;
    private String version;

    public ApplicationLauncher(String[] args) {
        Launcher.getLogger().log("[UPDATER] Checking defaults...");
        config.checkEntry("updater.installed.info.version","0");
        version = config.getString("updater.installed.info.version");
        config.checkEntry("updater.settings.autoUpdate",true);
        config.checkEntry("updater.settings.updateChannel","stable");
        autoUpdate = config.getBoolean("updater.settings.autoUpdate");
        updateChannel = config.getString("updater.settings.updateChannel");
    }

    public String getVersion() {
        return version;
    }

    public Config getConfig() {
        return config;
    }

    public String getLibraries() {
        return libraries;
    }

    public String getUpdateChannel() {
        return updateChannel;
    }

    public boolean automaticUpdates() {
        return autoUpdate;
    }

    private boolean validate() {
        Launcher.getLogger().log("[UPDATER] Validating installed version...");
        File folder = new File(libraries+"versions/");
        if(!folder.mkdirs()) {
            if(folder.exists()) {
                if(folder.isDirectory()) {
                    Launcher.getLogger().log("[UPDATER] Found versions folder...");
                    for(File versions: Objects.requireNonNull(folder.listFiles())) {
                        if(versions.isDirectory()) {
                            if(Objects.requireNonNull(versions.listFiles()).length <= 3) {
                                for (File jar : Objects.requireNonNull(versions.listFiles())) {
                                    Launcher.getLogger().log("[UPDATER] Found "+jar.getName());
                                    if (jar.getName().contains("nexus-" + version + ".jar")) {
                                        Launcher.getLogger().log("[UPDATER] Successfully validated installed version "+version+"!");
                                        return true;
                                    }
                                }
                            } else {
                                Launcher.getLogger().error("[UPDATER] Too many versions!");
                            }
                        }
                    }
                }
            }
        }
        FileUtil.deleteFolder(folder);
        return false;
    }

    public boolean update(String updateChannelId, boolean overwrite) {
        Launcher.getLogger().log("[UPDATER] Connecting to update channel "+updateChannelId+"...");
        try {
            JsonArray versions = new Gson().fromJson(GsonUtil.getFromURL("https://raw.githubusercontent.com/zyneonstudios/nexus-nex/main/application/index.json"), JsonObject.class).getAsJsonArray("versions");
            Launcher.getLogger().log("[UPDATER] Connected to server! Searching for channel...");
            for(JsonElement element: versions) {
                JsonObject version = element.getAsJsonObject();
                String id = version.get("meta").getAsJsonObject().get("id").getAsString();
                if(id.equals(updateChannelId)) {
                    Launcher.getLogger().log("[UPDATER] Found correct update channel!");
                    String newVersion = version.get("info").getAsJsonObject().get("version").getAsString();
                    Launcher.getLogger().log("[UPDATER] Installed version: "+this.version);
                    Launcher.getLogger().log("[UPDATER] Available version: "+newVersion);
                    Launcher.getLogger().log("[UPDATER] Need path creation: "+new File(libraries+"versions/"+id+"/").mkdirs());
                    File app = new File(libraries+"versions/"+id+"/nexus-"+newVersion+".jar");
                    if(app.exists()) {
                        if(overwrite) {
                            app.delete();
                        } else {
                            Launcher.getLogger().log("[UPDATER] Version "+this.version+" is already installed! Skipping update process...");
                            return true;
                        }
                    }
                    Launcher.getLogger().log("[UPDATER] Downloading...");
                    app = FileUtil.downloadFile(version.get("meta").getAsJsonObject().get("download").getAsString(),libraries+"versions/"+id+"/nexus-"+newVersion+".jar");
                    Launcher.getLogger().log("[UPDATER] Validating download...");
                    if(app != null) {
                        this.version = newVersion;
                        config.set("updater.installed.info.version", this.version);
                        return true;
                    } else {
                        Launcher.getLogger().error("[UPDATER] Couldn't download new version!");
                    }
                } else {
                    Launcher.getLogger().log("[UPDATER] Found update channel "+id+"...");
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return false;
    }

    public int launch(String updateChannelId) {
        if(validate()) {
            Launcher.getLogger().log("[UPDATER] Launching Zyneon Studios NEXUS Application v"+version+"...");
            try {
                ProcessBuilder processBuilder = new ProcessBuilder("java", "-jar", "--add-opens", "java.desktop/sun.awt=ALL-UNNAMED", "--add-opens", "java.desktop/sun.lwawt=ALL-UNNAMED", "--add-opens", "java.desktop/sun.lwawt.macosx=ALL-UNNAMED", libraries+"versions/"+updateChannelId+"/nexus-"+version+".jar", "application", "--add-opens java.desktop/sun.awt=ALL-UNNAMED --add-opens java.desktop/sun.lwawt=ALL-UNNAMED --add-opens java.desktop/sun.lwawt.macosx=ALL-UNNAMED");
                processBuilder.redirectErrorStream(true);
                Process process = processBuilder.start();
                Launcher.getSplash().setVisible(false);
                InputStream inputStream = process.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println(line);
                }
                return process.waitFor();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else {
            try {
                if(update(updateChannelId,true)) {
                    return launch(updateChannelId);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return -1;
    }
}
