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
            Main.getLogger().debug("TRYING TO UPDATE INSTANCE " + name + " (" + id + ")...");
            try {
                File pack = new File(path + "/pack.zip");
                File mods = new File(path + "/mods/");

                Main.getLogger().log("CHECKING FOR INSTANCE PACK FILE...");
                if (pack.exists()) {
                    Main.getLogger().log("FOUND OLD INSTANCE PACK FILE!");
                    Main.getLogger().log("DELETING OLD INSTANCE PACK FILE...");
                    if (pack.delete()) {
                        Main.getLogger().log("DELETED OLD INSTANCE PACK FILE!");
                    } else {
                        if (pack.delete()) {
                            Main.getLogger().log("DELETED OLD INSTANCE PACK FILE!");
                        } else {
                            Main.getLogger().log("COULDN'T DELETE OLD PACK FILE!");
                            throw new RuntimeException("COULDN'T DELETE OLD PACK FILE!");
                        }
                    }
                } else {
                    Main.getLogger().log("NO OLD INSTANCE PACK FILE FOUND!");
                }

                Main.getLogger().log("UPDATING INSTANCE JSON FILE...");
                String url = "https://raw.githubusercontent.com/danieldieeins/ZyneonApplicationContent/main/m/" + id + ".json";
                json = new Config(FileUtil.downloadFile(url, path + "/zyneonInstance.json"));
                version = json.getString("modpack.version");
                minecraftVersion = json.getString("modpack.minecraft");
                name = json.getString("modpack.name");
                Main.getLogger().log("UPDATED INSTANCE JSON FILE!");

                Main.getLogger().log("DOWNLOADING NEW INSTANCE PACK FILE...");
                pack = FileUtil.downloadFile(json.getString("modpack.download"), path + "/pack.zip");
                Main.getLogger().log("DOWNLOADED NEW INSTANCE PACK FILE!");

                Main.getLogger().log("DELETING OLD MODS...");
                if (!mods.mkdirs()) {
                    FileUtil.deleteFolder(mods);
                }
                Main.getLogger().log("DELETED OLD MODS!");

                Main.getLogger().log("UNZIPPING NEW INSTANCE PACK FILE...");
                if (FileUtil.unzipFile(pack.getPath(), path)) {
                    Main.getLogger().log("SUCCESSFULLY UNZIPPED NEW INSTANCE PACK FILE!");
                } else {
                    Main.getLogger().log("COULDN'T UNZIP NEW INSTANCE PACK FILE!");
                    throw new RuntimeException("COULDN'T UNZIP NEW INSTANCE PACK FILE!");
                }
            } catch (Exception e) {
                Main.getLogger().debug("COULDN'T UPDATE INSTANCE, TRYING TO CANCEL START PROCESS...");
                throw new RuntimeException(e.getMessage());
            }
            Main.getLogger().debug("SUCCESSFULLY UPDATED INSTANCE!");
        }
        return true;
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