package com.zyneonstudios.application.backend.instance;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;
import com.moandjiezana.toml.Toml;
import com.zyneonstudios.Main;
import com.zyneonstudios.application.Application;
import live.nerotv.shademebaby.file.Config;
import live.nerotv.shademebaby.utils.FileUtil;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Enumeration;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class VanillaInstance implements Instance {

    private Config json;
    private Config settings;
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
        settings = new Config(path+"zyneonSettings.json");
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
        CompletableFuture.runAsync(()->{
            File modsFolder = new File(path+"mods/");
            if(modsFolder.exists()) {
                try {
                    File[] mods = modsFolder.listFiles();
                    File listFile = new File(path+"cache/zyneon.modlist.json");
                    if(listFile.exists()) {
                        Main.getLogger().debug("Delete previous sync: "+listFile.delete());
                    }
                    Config modList = new Config(listFile.getAbsolutePath());
                    for(File mod:mods) {
                        if(mod.getName().endsWith(".jar")) {
                            JarFile modJar = new JarFile(mod.getAbsolutePath());
                            Enumeration<JarEntry> entries = modJar.entries();
                            boolean saved = false;
                            while (entries.hasMoreElements()) {
                                JarEntry entry = entries.nextElement();
                                if (entry.getName().equals("fabric.mod.json")) {
                                    try {
                                        InputStream inputStream = modJar.getInputStream(entry);
                                        JsonReader jsonReader = new JsonReader(new InputStreamReader(inputStream));
                                        Gson gson = new Gson();
                                        JsonObject json = gson.fromJson(jsonReader, JsonObject.class);
                                        String id = json.get("id").getAsString();
                                        modList.set("mods." + id + ".id", id);
                                        modList.set("mods." + id + ".loader", "fabric/json");
                                        modList.set("mods." + id + ".version", json.get("version").getAsString());
                                        modList.set("mods." + id + ".file", mod.getName());
                                        if (json.get("name") != null) {
                                            modList.set("mods." + id + ".name", json.get("name").getAsString());
                                        } else {
                                            modList.set("mods." + id + ".name", id);
                                        }
                                        modList.set("files." + mod.getName().replace(".","::"), id);
                                    } catch (Exception e) {
                                        Main.getLogger().error("Couldn't read fabric json file (" + mod.getName() + "): " + e.getMessage());
                                    }
                                    saved = true;
                                    break;
                                } else if (entry.getName().equals("META-INF/mods.toml")) {
                                    try {
                                        Toml toml = new Toml().read(new String(modJar.getInputStream(entry).readAllBytes()));
                                        Gson gson = new GsonBuilder().setPrettyPrinting().create();
                                        String jsonString = gson.toJson(toml.toMap());
                                        JsonObject json = gson.fromJson(jsonString, JsonObject.class).get("mods").getAsJsonArray().get(0).getAsJsonObject();
                                        String id = json.get("modId").getAsString();
                                        modList.set("mods." + id + ".id", id);
                                        modList.set("mods." + id + ".loader", "forge/toml");
                                        if (json.get("version") != null) {
                                            modList.set("mods." + id + ".version", json.get("version").getAsString());
                                        } else {
                                            modList.set("mods." + id + ".version", "null");
                                        }
                                        modList.set("mods." + id + ".file", mod.getName());
                                        if (json.get("displayName") != null) {
                                            modList.set("mods." + id + ".name", json.get("displayName").getAsString());
                                        } else {
                                            modList.set("mods." + id + ".name", id);
                                        }
                                        modList.set("files." + mod.getName().replace(".","::"), id);
                                    } catch (Exception e) {
                                        Main.getLogger().error("Couldn't read forge toml file (" + mod.getName() + "): " + e.getMessage());
                                    }
                                    saved = true;
                                    break;
                                } else if (entry.getName().equals("mcmod.info")) {
                                    try {
                                        InputStream inputStream = modJar.getInputStream(entry);
                                        JsonReader jsonReader = new JsonReader(new InputStreamReader(inputStream));
                                        Gson gson = new Gson();
                                        JsonArray array = gson.fromJson(jsonReader, JsonArray.class);
                                        JsonObject json = array.get(0).getAsJsonObject();
                                        String id = json.get("modid").getAsString();
                                        modList.set("mods." + id + ".id", id);
                                        modList.set("mods." + id + ".loader", "forge/json");
                                        if (json.get("version") != null) {
                                            modList.set("mods." + id + ".version", json.get("version").getAsString());
                                        } else {
                                            modList.set("mods." + id + ".version", "null");
                                        }
                                        modList.set("mods." + id + ".file", mod.getName());
                                        if (json.get("name") != null) {
                                            modList.set("mods." + id + ".name", json.get("name").getAsString());
                                        } else {
                                            modList.set("mods." + id + ".name", id);
                                        }
                                        modList.set("files." + mod.getName().replace(".","::"), id);
                                    } catch (Exception e) {
                                        Main.getLogger().error("Couldn't read forge json file (" + mod.getName() + "): " + e.getMessage());
                                    }
                                    saved = true;
                                    break;
                                }
                            }
                            if(!saved) {
                                modList.set("files." + mod.getName().replace(".","::"), "zyneon:unknown-mod");
                            }
                        }
                    }
                } catch (Exception e) {
                    Main.getLogger().error("Couldn't sync mod files: "+e.getMessage());
                }
            }
        });
    }

    @Override
    public Config getJSON() {
        return json;
    }

    @Override
    public Config getSettings() {
        return settings;
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
        settings = null;
        version = null;
        minecraftVersion = null;
        System.gc();
    }
}