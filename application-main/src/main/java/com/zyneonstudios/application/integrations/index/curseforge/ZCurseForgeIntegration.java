package com.zyneonstudios.application.integrations.index.curseforge;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.zyneonstudios.Main;
import com.zyneonstudios.application.Application;
import com.zyneonstudios.application.integrations.index.zyndex.ZyndexIntegration;
import com.zyneonstudios.application.utils.ZLogger;
import com.zyneonstudios.application.utils.backend.MinecraftVersion;
import fr.flowarg.flowupdater.download.json.CurseModPackInfo;
import fr.flowarg.flowupdater.download.json.Mod;
import fr.flowarg.flowupdater.integrations.curseforgeintegration.CurseForgeIntegration;
import fr.flowarg.flowupdater.integrations.curseforgeintegration.CurseModPack;
import fr.flowarg.flowupdater.utils.IOUtils;
import fr.flowarg.openlauncherlib.NoFramework;
import live.nerotv.shademebaby.file.Config;
import live.nerotv.shademebaby.utils.FileUtil;
import live.nerotv.shademebaby.utils.StringUtil;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ZCurseForgeIntegration extends CurseForgeIntegration {

    private ZLogger logger;
    private String pathString;
    private int id;
    private int v;
    private Path instancePath;
    private Path cachePath;
    private Path modsPath;

    public ZCurseForgeIntegration(ZLogger logger, int id, int v) throws Exception {
        super(logger, Path.of(Application.getInstancePath()+"instances/curseforge-"+id+"-"+v+"/cache/"));
        this.logger = logger;
        this.id = id;
        this.v = v;
        pathString = Application.getInstancePath()+"instances/curseforge-"+id+"-"+v+"/";

        instancePath = Path.of(pathString);
        cachePath = Path.of(pathString+"cache/");
        modsPath = Path.of(pathString+"mods/");
    }

    public static String makeRequest(String url) {
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection)new URL(url).openConnection();
            connection.setRequestMethod("GET");
            connection.setInstanceFollowRedirects(true);
            connection.setUseCaches(false);
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("x-api-key", "$2a$10$DJiIWDCef9nkUl0fchY9eecGQunflMcS/TxFMn5Ng68cX5KpGOaEC");
            return IOUtils.getContent(connection.getInputStream());
        } catch (Exception e) {
            return null;
        } finally {
            if(connection != null) {
                connection.disconnect();
            }
        }
    }

    public static String getVersionId(int id, String ve) {
        JsonObject root = new Gson().fromJson(ZCurseForgeIntegration.makeRequest("https://api.curseforge.com/v1/mods/"+id+"/files?gameVersion="+ve), JsonObject.class);
        JsonArray array = root.get("data").getAsJsonArray();
        return array.get(0).getAsJsonObject().get("id").getAsString();
    }

    public static String getVersionId(int id, String ve, NoFramework.ModLoader loader) {
        JsonObject root = new Gson().fromJson(ZCurseForgeIntegration.makeRequest("https://api.curseforge.com/v1/mods/"+id+"/files?modLoaderType="+loader.toString()+"&gameVersion="+ve), JsonObject.class);
        JsonArray array = root.get("data").getAsJsonArray();
        return array.get(0).getAsJsonObject().get("id").getAsString();
    }

    public Path getCachePath() {
        return cachePath;
    }

    public Path getInstancePath() {
        return instancePath;
    }

    public Path getModsPath() {
        return modsPath;
    }

    public String getPathString() {
        return pathString;
    }

    public int getID() {
        return id;
    }

    public ZLogger getLogger() {
        return logger;
    }

    public void install(int version) {
        logger.log("[CURSEFORGE] (INTEGRATION) Starting installation of CurseForge modpack "+id+"...");
        CompletableFuture.runAsync(()->{
            try {
                logger.log("[CURSEFORGE] (INTEGRATION) Getting modpack info for version "+version+"...");
                CurseModPackInfo info = new CurseModPackInfo(id,version,false);
                logger.log("[CURSEFORGE] (INTEGRATION) Resolving "+id+"-"+version+"...");
                CurseModPack pack = getCurseModPack(info);
                String packName = pack.getName();
                String packVersion = pack.getVersion();
                logger.log("[CURSEFORGE] (INTEGRATION) Resolved modpack "+packName+" v"+packVersion+"!");
                List<CurseModPack.CurseModPackMod> packFiles = pack.getMods();
                logger.log("[CURSEFORGE] (INTEGRATION) Starting download of "+packFiles.size()+" files...");
                downloadFiles(packFiles);
                File cache = new File(cachePath.toUri());
                if(cache.exists()) {
                    if(cache.isDirectory()) {
                        File[] elements = cache.listFiles();
                        assert elements != null;
                        for(File element:elements) {
                            if(element.getName().toLowerCase().endsWith(".zip")) {
                                File temp = new File(Main.getDirectoryPath()+"temp/");
                                temp.mkdirs();
                                String tempID = StringUtil.generateAlphanumericString(16);
                                Main.getLogger().debug(element.getName()+" to "+Main.getDirectoryPath()+"temp/"+tempID+"/");
                                new File(Main.getDirectoryPath()+"temp/"+tempID+"/").mkdirs();
                                if(FileUtil.unzipFile(element.getAbsolutePath(),Main.getDirectoryPath()+"temp/"+tempID+"/")) {
                                    if(new File(Main.getDirectoryPath()+"temp/"+tempID+"/overrides/").exists()) {
                                        if(new File(Main.getDirectoryPath()+"temp/"+tempID+"/overrides/").isDirectory()) {
                                            File zip = new File(Main.getDirectoryPath()+"temp/"+tempID+"/overrides/");
                                            FileUtils.copyDirectory(zip,new File(pathString));
                                        }
                                    }
                                }
                            }
                            System.gc();
                        }
                    }
                }
                logger.debug(" ");
                logger.log("[CURSEFORGE] (INTEGRATION) Building zyneonInstance file from CurseForge data...");
                Config instance = new Config(pathString+"zyneonInstance.json");
                instance.set("modpack.id","curseforge-"+id+"-"+v);
                instance.set("modpack.name",packName);
                instance.set("modpack.version",packVersion);
                logger.log("[CURSEFORGE] (INTEGRATION) Gathering modloader and Minecraft infos...");
                Config curseforge = new Config(new File(pathString+"manifest.json"));
                String modloader = "Vanilla";
                String mlversion = "No mods";
                String minecraft = curseforge.getString("minecraft.version");
                if(curseforge.get("minecraft.modLoaders")!=null) {
                    JsonArray loaders = new Gson().fromJson(curseforge.get("minecraft.modLoaders").toString(),JsonArray.class);
                    for (int i = 0; i < loaders.size(); i++) {
                        JsonObject loader = loaders.get(i).getAsJsonObject();
                        String id = loader.get("id").getAsString();
                        if(id.startsWith("forge-")) {
                            modloader = "Forge";
                            mlversion = id.replace("forge-","");
                            instance.set("modpack.forge.version",mlversion);
                            instance.set("modpack.forge.type", MinecraftVersion.getForgeType(minecraft));
                        } else if(id.startsWith("fabric-")) {
                            modloader = "Fabric";
                            mlversion = id.replace("fabric-","");
                            instance.set("modpack.fabric",mlversion);
                        }
                    }
                }
                logger.log("[CURSEFORGE] (INTEGRATION) Found "+modloader+" ("+mlversion+") for Minecraft "+minecraft+"!");
                instance.set("modpack.minecraft",minecraft);
                instance.set("modpack.instance","instances/curseforge-"+id+"-"+v);
                String description = "This is a modpack instance downloaded from CurseForge!";
                if(curseforge.getString("description")!=null) {
                    description = curseforge.getString("description");
                }
                instance.set("modpack.description",description);
                ZyndexIntegration.convert(instance.getJsonFile());
                logger.log("[CURSEFORGE] (INTEGRATION) Successfully built zyneonInstance file!");
                logger.log("[CURSEFORGE] (INTEGRATION) Installed CurseForge modpack "+packName+" v"+packVersion+"!");
                Application.loadInstances();
                Application.getFrame().getBrowser().loadURL(Application.getInstancesURL()+"?tab=curseforge-"+id+"-"+v);
            } catch (Exception e) {
                e.printStackTrace();
                logger.debug(Arrays.stream(e.getStackTrace()).toList().getFirst().toString());
                logger.error("[CURSEFORGE] (INTEGRATION) Couldn't initialise CurseForge modpack: "+e.getMessage());
                Application.getFrame().getBrowser().loadURL(Application.getInstancesURL());
            }
        });
    }

    private void downloadFiles(List<CurseModPack.CurseModPackMod> files) {
        for(Mod file:files) {
            String name = file.getName();
            String url = file.getDownloadURL();
            String sha1 = file.getSha1();
            long size = file.getSize();
            logger.debug(" ");
            logger.debug("[CURSEFORGE] (INTEGRATION) Preparing "+name+" from: "+url+"...");
            logger.debug("[CURSEFORGE] (INTEGRATION) Data: "+sha1+" ("+size+")");
            if(name.contains("/")) {
                logger.debug("[CURSEFORGE] (INTEGRATION) Created destination folder: "+new File(instancePath.toString()+"/"+name).getParentFile().mkdirs());
                logger.debug("[CURSEFORGE] (INTEGRATION) Downloading...");
                try {
                    logger.debug("[CURSEFORGE] (INTEGRATION) Downloaded "+ FileUtil.downloadFile(url, instancePath.toString() +"/"+ name).getAbsolutePath()+"!");
                } catch (Exception e) {
                    logger.error("[CURSEFORGE] (INTEGRATION) Couldn't download "+name+" from "+url+": "+e.getMessage());
                }
            } else {
                logger.debug("[CURSEFORGE] (INTEGRATION) Created destination folder: " + new File(modsPath.toString() +"/"+ name).getParentFile().mkdirs());
                logger.debug("[CURSEFORGE] (INTEGRATION) Downloading...");
                try {
                    logger.debug("[CURSEFORGE] (INTEGRATION) Downloaded "+FileUtil.downloadFile(url, modsPath.toString() +"/"+ name));
                } catch (Exception e) {
                    logger.error("[CURSEFORGE] (INTEGRATION) Couldn't download " + name + " from " + url + ": " + e.getMessage());
                }
            }
        }
    }
}