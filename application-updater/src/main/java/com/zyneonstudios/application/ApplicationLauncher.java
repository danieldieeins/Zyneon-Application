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
    private String version;

    public ApplicationLauncher(String[] args) {
        config.checkEntry("updater.installed.info.version","0");
        version = config.getString("updater.installed.info.version");
    }

    private boolean validate() {
        File folder = new File(libraries+"versions/");
        if(!folder.mkdirs()) {
            if(folder.exists()) {
                if(folder.isDirectory()) {
                    for(File versions: Objects.requireNonNull(folder.listFiles())) {
                        if(versions.isDirectory()) {
                            if(Objects.requireNonNull(versions.listFiles()).length <= 3) {
                                for (File jar : Objects.requireNonNull(versions.listFiles())) {
                                    if (jar.getName().contains("nexus-" + version + ".jar")) {
                                        return true;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        FileUtil.deleteFolder(folder);
        return false;
    }

    public boolean update(String updateChannelId) {
        try {
            JsonArray versions = new Gson().fromJson(GsonUtil.getFromURL("https://raw.githubusercontent.com/zyneonstudios/nexus-nex/main/application/index.json"), JsonObject.class).getAsJsonArray("versions");
            for(JsonElement element: versions) {
                JsonObject version = element.getAsJsonObject();
                String id = version.get("meta").getAsJsonObject().get("id").getAsString();
                if(id.equals(updateChannelId)) {
                    String newVersion = version.get("info").getAsJsonObject().get("version").getAsString();
                    File app = FileUtil.downloadFile(version.get("meta").getAsJsonObject().get("download").getAsString(),libraries+"versions/"+id+"/nexus-"+newVersion+".jar");
                    if(app != null) {
                        this.version = newVersion;
                        config.set("updater.installed.info.version", this.version);
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return false;
    }

    public int launch() {
        if(validate()) {
            try {
                ProcessBuilder processBuilder = new ProcessBuilder("java", "-jar", "--add-opens", "java.desktop/sun.awt=ALL-UNNAMED", "--add-opens", "java.desktop/sun.lwawt=ALL-UNNAMED", "--add-opens", "java.desktop/sun.lwawt.macosx=ALL-UNNAMED", libraries+"versions/nexus-"+version+".jar", "application", "--add-opens java.desktop/sun.awt=ALL-UNNAMED --add-opens java.desktop/sun.lwawt=ALL-UNNAMED --add-opens java.desktop/sun.lwawt.macosx=ALL-UNNAMED");
                processBuilder.redirectErrorStream(true);
                Process process = processBuilder.start();
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
                if(update("stable")) {
                    return launch();
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return -1;
    }
}
