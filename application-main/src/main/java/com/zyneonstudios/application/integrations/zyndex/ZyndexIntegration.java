package com.zyneonstudios.application.integrations.zyndex;

import com.zyneonstudios.Main;
import com.zyneonstudios.application.Application;
import com.zyneonstudios.application.integrations.zyndex.instance.WritableInstance;
import com.zyneonstudios.nexus.index.ReadableZyndex;
import com.zyneonstudios.nexus.instance.ReadableZynstance;
import live.nerotv.shademebaby.file.Config;
import live.nerotv.shademebaby.utils.FileUtil;

import java.io.File;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class ZyndexIntegration {

    public static boolean install(ReadableZynstance onlineInstance) {
        try {
            String url = onlineInstance.getLocation();
            File instance = new File(Application.getInstancePath() + "instances/" + onlineInstance.getId() + "/");
            Main.getLogger().debug("[CONNECTOR] Created instance path: " + instance.mkdirs());
            FileUtil.downloadFile(url, URLDecoder.decode(instance.getAbsolutePath() + "/zyneonInstance.json", StandardCharsets.UTF_8));
            Application.loadInstances();
            Application.getFrame().getBrowser().loadURL(Application.getInstancesURL() + "?tab=" + onlineInstance.getId());
            return true;
        } catch (Exception e) {
            Main.getLogger().err("[ZYNDEX] Couldn't install instance: " + e.getMessage());
            return false;
        }
    }

    public static boolean install(ReadableZynstance onlineInstance, String id) {
        try {
            String url = onlineInstance.getLocation();
            File instanceDirectory = new File(Application.getInstancePath() + "instances/" + id + "/");
            Main.getLogger().debug("[CONNECTOR] Created instance path: " + instanceDirectory.mkdirs());
            WritableInstance instance = new WritableInstance(FileUtil.downloadFile(url, URLDecoder.decode(instanceDirectory.getAbsolutePath() + "/zyneonInstance.json", StandardCharsets.UTF_8)));
            instance.setId(id);
            Application.loadInstances();
            Application.getFrame().getBrowser().loadURL(Application.getInstancesURL() + "?tab=" + instance.getId());
            return true;
        } catch (Exception e) {
            Main.getLogger().err("[ZYNDEX] Couldn't install instance: " + e.getMessage());
            return false;
        }
    }

    public static WritableInstance update(WritableInstance localInstance) {
        try {
            ReadableZynstance onlineInstance;
            try {
                onlineInstance = new ReadableZynstance(localInstance.getLocation());
            } catch (Exception e) {
                onlineInstance = null;
            }
            Config instanceConfig = new Config(localInstance.getFile());
            boolean fixConverted = false;
            if(instanceConfig.get("converted")!=null) {
                if(instanceConfig.getBool("converted")) {
                    ReadableZyndex nex = new ReadableZyndex("https://zyneonstudios.github.io/nexus-nex/zyndex/index.json");
                    if(nex.getZynstances().containsKey(localInstance.getId())) {
                        onlineInstance = nex.getZynstances().get(localInstance.getId());
                        fixConverted = true;
                    }
                }
            }
            if (fixConverted || !localInstance.getVersion().equals(onlineInstance.getVersion()) || !new File(localInstance.getPath() + "meta/pack.zip").exists()) {
                if (onlineInstance.getDownloadUrl() != null) {
                    Main.getLogger().log("[ZYNDEX] Trying to update" + onlineInstance.getName() + " (" + onlineInstance.getId() + ")...");
                    try {
                        String path = Application.getInstancePath() + "instances/" + onlineInstance.getId() + "/";
                        File pack = new File(path + "meta/pack.zip");
                        File mods = new File(path + "mods/");
                        File libraries = new File(path + "libraries/");
                        Main.getLogger().log("[ZYNDEX] Deleting old libraries...");
                        String lT = "!";
                        try {
                            FileUtil.deleteFolder(libraries);
                        } catch (Exception e) {
                            lT = ": "+e.getMessage();
                        }
                        if(!libraries.exists()) {
                            Main.getLogger().log("[ZYNDEX] Old libraries deleted!");
                        } else {
                            Main.getLogger().error("[ZYNDEX] Couldn't delete old libraries"+lT);
                        }
                        Main.getLogger().log("[ZYNDEX] Checking if old pack file exists...");
                        if (pack.exists()) {
                            Main.getLogger().log("[ZYNDEX] Found old pack file!");
                            Main.getLogger().log("[ZYNDEX] Deleting old pack file...");
                            if (pack.delete()) {
                                Main.getLogger().log("[ZYNDEX] Deleted old pack file!");
                            } else {
                                Main.getLogger().debug("[ZYNDEX] Failed to delete old pack file. Trying again...");
                                if (pack.delete()) {
                                    Main.getLogger().log("[ZYNDEX] Deleted old pack file!");
                                } else {
                                    Main.getLogger().error("[ZYNDEX] Couldn't delete old pack file...");
                                    throw new RuntimeException("Couldn't delete file...");
                                }
                            }
                        } else {
                            Main.getLogger().log("[ZYNDEX] No old pack file found!");
                        }
                        Main.getLogger().log("[ZYNDEX] Updating json file...");
                        String url = onlineInstance.getLocation();
                        localInstance = new WritableInstance(FileUtil.downloadFile(url, path + "/zyneonInstance.json"));
                        Main.getLogger().log("[ZYNDEX] Updated json file!");
                        Main.getLogger().log("[ZYNDEX] Downloading new pack file...");
                        pack = FileUtil.downloadFile(onlineInstance.getDownloadUrl(), path + "meta/pack.zip");
                        Main.getLogger().log("[ZYNDEX] New pack file downloaded!");
                        Main.getLogger().log("[ZYNDEX] Deleting old mods...");
                        String mT = "!";
                        try {
                            FileUtil.deleteFolder(mods);
                        } catch (Exception e) {
                            mT = ": "+e.getMessage();
                        }
                        if(!mods.exists()) {
                            Main.getLogger().log("[ZYNDEX] Old mods deleted!");
                        } else {
                            Main.getLogger().error("[ZYNDEX] Couldn't delete old mods"+lT);
                        }
                        Main.getLogger().log("[ZYNDEX] Old mods deleted!");
                        Main.getLogger().log("[ZYNDEX] Unzipping pack file...");
                        if (FileUtil.unzipFile(pack.getPath(), path)) {
                            Main.getLogger().log("[ZYNDEX] Pack file unzipped!");
                        } else {
                            Main.getLogger().error("[ZYNDEX] Failed to unzip pack file!");
                            throw new RuntimeException("Couldn't unzip file...");
                        }

                        if(onlineInstance.getBackgroundUrl()!=null||onlineInstance.getIconUrl()!=null||onlineInstance.getLogoUrl()!=null||onlineInstance.getThumbnailUrl()!=null) {
                            WritableInstance writableInstance = new WritableInstance(localInstance.getFile());
                            if(onlineInstance.getBackgroundUrl()!=null) {
                                Main.getLogger().log("[ZYNDEX] Downloading background...!");
                                File background = FileUtil.downloadFile(onlineInstance.getBackgroundUrl(), path + "meta/background.png");
                                writableInstance.setBackground("file://" + background.getAbsolutePath().replace("\\\\","\\").replace("\\","/"));
                            }
                            if(onlineInstance.getIconUrl()!=null) {
                                Main.getLogger().log("[ZYNDEX] Downloading icon...!");
                                File icon = FileUtil.downloadFile(onlineInstance.getIconUrl(), path + "meta/icon.png");
                                writableInstance.setIconUrl("file://" + icon.getAbsolutePath().replace("\\\\","\\").replace("\\","/"));
                            }
                            if(onlineInstance.getLogoUrl()!=null) {
                                Main.getLogger().log("[ZYNDEX] Downloading logo...!");
                                File logo = FileUtil.downloadFile(onlineInstance.getLogoUrl(), path + "meta/logo.png");
                                writableInstance.setLogoUrl("file://" + logo.getAbsolutePath().replace("\\\\","\\").replace("\\","/"));
                            }
                            if(onlineInstance.getThumbnailUrl()!=null) {
                                Main.getLogger().log("[ZYNDEX] Downloading thumbnail...!");
                                File thumbnail = FileUtil.downloadFile(onlineInstance.getThumbnailUrl(), path + "meta/thumbnail.png");
                                writableInstance.setThumbnailUrl("file://" + thumbnail.getAbsolutePath().replace("\\\\","\\").replace("\\","/"));
                            }
                        }
                    } catch (Exception e) {
                        Main.getLogger().error("[ZYNDEX] Couldn't update. Trying to cancel start...");
                        throw new RuntimeException(e.getMessage());
                    }
                    Main.getLogger().debug("[ZYNDEX] Updated instance!");
                }
                System.out.println(onlineInstance.getMinecraftVersion());
                localInstance.setMinecraftVersion(onlineInstance.getMinecraftVersion());
                return localInstance;
            }
        } catch (Exception e) {
            Main.getLogger().err("[ZYNDEX] Couldn't update " + localInstance.getName() + " (" + localInstance.getId() + "): " + e.getMessage());
        }
        return null;
    }

    public static ArrayList<ReadableZynstance> search(ReadableZyndex zyndex, String query, String minecraftVersion) {
        minecraftVersion = minecraftVersion.replace(".", "-");
        query = format(query);
        ArrayList<ReadableZynstance> results = new ArrayList<>();
        if (zyndex.getInstances() != null) {
            if (!zyndex.getInstances().isEmpty()) {
                for (ReadableZynstance instance : zyndex.getInstances()) {
                    String result = format(instance.getName());
                    if (result.contains(query) && !instance.isHidden()) {
                        if (minecraftVersion.equalsIgnoreCase(instance.getMinecraftVersion().replace(".", "-")) || minecraftVersion.equalsIgnoreCase("all")) {
                            results.add(instance);
                        }
                    } else {
                        String id = format(instance.getId());
                        if(query.equals(id)) {
                            results.add(instance);
                        }
                    }
                }
            }
        }
        return results;
    }

    private static String format(String in) {
        in = in
                .replace(".", "")
                .replace("_", "%")
                .replace("/", "%")
                .replace(" ", "%")
                .replace("\"", "%")
                .replace("\\", "%");
        return in.toLowerCase();
    }

    public static File convert(File oldInstanceFile) {
        try {
            Config instance = new Config(oldInstanceFile);

            //INFO - AUTHOR
            if (instance.get("modpack.author") != null) {
                instance.set("instance.info.author", instance.getString("modpack.author"));
            } else {
                instance.set("instance.info.author", "unknown");
            }

            //INFO - DESCRIPTION
            if (instance.get("modpack.description") != null) {
                instance.set("instance.info.description", instance.getString("modpack.description"));
            } else {
                instance.set("instance.info.description", "This instance is converted!");
            }

            //INFO - NAME
            instance.set("instance.info.name", instance.getString("modpack.name"));

            //INFO - VERSION
            instance.set("instance.info.version", instance.getString("modpack.version"));


            //META - DOWNLOAD
            instance.set("instance.meta.download", "null");

            //META - FORGE TYPE
            if (instance.get("modpack.forge.type") != null) {
                instance.set("instance.meta.forgeType", instance.getString("modpack.forge.type"));
            }

            //META - ID
            instance.set("instance.meta.id", instance.getString("modpack.id"));

            //META - IS HIDDEN?
            instance.set("instance.meta.isHidden", false);

            //META - LOCATION
            instance.set("instance.meta.location", "local");

            //META - ORIGIN
            instance.set("instance.meta.origin", "unknown");


            //RESOURCES - BACKGROUND
            if (instance.get("modpack.background") != null) {
                instance.set("instance.resources.background", instance.getString("modpack.background"));
            }

            //RESOURCES - ICON
            if (instance.get("modpack.icon") != null) {
                instance.set("instance.resources.icon", instance.getString("modpack.icon"));
            }

            //RESOURCES - LOGO
            if (instance.get("modpack.logo") != null) {
                instance.set("instance.resources.logo", instance.getString("modpack.logo"));
            }

            //RESOURCES - THUMBNAIL
            if (instance.get("modpack.thumbnail") != null) {
                instance.set("instance.resources.thumbnail", instance.getString("modpack.thumbnail"));
            }


            //VERSIONS - FABRIC
            if (instance.get("modpack.fabric") != null) {
                instance.set("instance.versions.fabric", instance.getString("modpack.fabric"));
            }

            //VERSIONS - FORGE
            if (instance.get("modpack.forge.version") != null) {
                instance.set("instance.versions.forge", instance.getString("modpack.forge.version"));
            }

            //VERSIONS - GAME
            instance.set("instance.versions.minecraft", instance.getString("modpack.minecraft"));


            //FILE DATA
            instance.delete("scheme");
            instance.set("scheme", "2024.3");
            instance.set("converted", true);
            instance.delete("modpack");

            return instance.getJsonFile();
        } catch (Exception e) {
            Main.getLogger().err("[ZYNDEX] Couldn't convert old instance: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }
}