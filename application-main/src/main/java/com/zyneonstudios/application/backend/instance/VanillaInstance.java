package com.zyneonstudios.application.backend.instance;

import com.zyneonstudios.Main;
import com.zyneonstudios.application.Application;
import live.nerotv.shademebaby.file.Config;
import live.nerotv.shademebaby.utils.FileUtil;

import java.io.File;
import java.util.UUID;

public class VanillaInstance implements Instance {

    private Config json;
    private String path;
    private String id;
    private String name;
    private String version;
    private String minecraftVersion;

    public VanillaInstance(Config json) {
        this.json = json;
        id = json.getString("modpack.id");
        name = json.getString("modpack.name");
        version = json.getString("modpack.version");
        minecraftVersion = json.getString("modpack.minecraft");
        path = Application.getInstancePath() + json.getString("modpack.instance");
    }

    @Override @SuppressWarnings("all")
    public boolean checkVersion() {
        try {
            String url = "https://raw.githubusercontent.com/danieldieeins/ZyneonApplicationContent/main/m/" + id + ".json";
            new File(Main.getDirectoryPath()+"temp/").mkdirs();
            Config json = new Config(FileUtil.downloadFile(url, Main.getDirectoryPath() + "temp/" + UUID.randomUUID() + ".json"));
            String version = json.getString("modpack.version");
            String installed = this.json.getString("modpack.version");
            json.getJsonFile().delete();
            if (!version.equals(installed)) {
                return false;
            }
        } catch (Exception ignore) {}
        return true;
    }

    @Override
    public boolean update() {
        if(json.getString("modpack.download")!=null) {
            Main.getLogger().log("[INSTANCE] Trying to update" + name + " (" + id + ")...");
            try {
                File pack = new File(path + "/pack.zip");
                File mods = new File(path + "/mods/");

                Main.getLogger().log("[INSTANCE] Checking if old pack file exists...");
                if (pack.exists()) {
                    Main.getLogger().log("[INSTANCE] Found old pack file!");
                    Main.getLogger().log("[INSTANCE] Deleting old pack file...");
                    if (pack.delete()) {
                        Main.getLogger().log("[INSTANCE] Deleted old pack file!");
                    } else {
                        Main.getLogger().debug("[INSTANCE] Failed to delete old pack file. Trying again...");
                        if (pack.delete()) {
                            Main.getLogger().log("[INSTANCE] Deleted old pack file!");
                        } else {
                            Main.getLogger().error("[INSTANCE] Couldn't delete old pack file...");
                            throw new RuntimeException("Couldn't delete file...");
                        }
                    }
                } else {
                    Main.getLogger().log("[INSTANCE] No old pack file found!");
                }

                Main.getLogger().log("[INSTANCE] Updating json file...");
                String url = "https://raw.githubusercontent.com/danieldieeins/ZyneonApplicationContent/main/m/" + id + ".json";
                json = new Config(FileUtil.downloadFile(url, path + "/zyneonInstance.json"));
                version = json.getString("modpack.version");
                minecraftVersion = json.getString("modpack.minecraft");
                name = json.getString("modpack.name");
                Main.getLogger().log("[INSTANCE] Updated json file!");

                Main.getLogger().log("[INSTANCE] Downloading new pack file...");
                pack = FileUtil.downloadFile(json.getString("modpack.download"), path + "/pack.zip");
                Main.getLogger().log("[INSTANCE] New pack file downloaded!");

                Main.getLogger().log("[INSTANCE] Deleting old mods...");
                if (!mods.mkdirs()) {
                    FileUtil.deleteFolder(mods);
                }
                Main.getLogger().log("[INSTANCE] Old mods deleted!");

                Main.getLogger().log("[INSTANCE] Unzipping pack file...");
                if (FileUtil.unzipFile(pack.getPath(), path)) {
                    Main.getLogger().log("[INSTANCE] Pack file unzipped!");
                } else {
                    Main.getLogger().error("[INSTANCE] Failed to unzip pack file!");
                    throw new RuntimeException("Couldn't unzip file...");
                }
            } catch (Exception e) {
                Main.getLogger().error("[INSTANCE] Couldn't update. Trying to cancel start...");
                throw new RuntimeException(e.getMessage());
            }
            Main.getLogger().debug("[INSTANCE] Updated instance!");
        }
        return true;
    }

    @Override
    public void sync() {
        File modsFolder = new File(path+"mods/");
        if(modsFolder.exists()) {
            try {
                File[] mods = modsFolder.listFiles();
                for(File mod:mods) {
                    if(mod.getName().endsWith(".jar")) {
                        System.out.println("MOD GEFUNDEN: "+mod.getAbsolutePath());
                    }
                }
            } catch (Exception e) {
                Main.getLogger().error("Couldn't sync mod files: "+e.getMessage());
            }
        }
    }

    @Override
    public Config getJSON() {
        return json;
    }

    @Override
    public String getPath() {
        return path;
    }

    @Override
    public String getID() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getVersion() {
        return version;
    }

    @Override
    public String getMinecraftVersion() {
        return minecraftVersion;
    }

    public void unload() {
        id = null;
        path = null;
        name = null;
        json = null;
        version = null;
        minecraftVersion = null;
        System.gc();
    }
}