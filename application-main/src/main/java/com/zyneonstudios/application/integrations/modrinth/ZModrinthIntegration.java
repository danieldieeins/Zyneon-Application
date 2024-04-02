package com.zyneonstudios.application.integrations.modrinth;

import com.zyneonstudios.application.Application;
import com.zyneonstudios.application.integrations.modrinth.flowarg.ModrinthIntegration;
import com.zyneonstudios.application.integrations.modrinth.flowarg.ModrinthModPack;
import com.zyneonstudios.application.integrations.modrinth.flowarg.ModrinthModPackInfo;
import com.zyneonstudios.application.utils.ZLogger;
import com.zyneonstudios.application.utils.backend.MinecraftVersion;
import fr.flowarg.flowupdater.download.json.Mod;
import live.nerotv.shademebaby.file.Config;
import live.nerotv.shademebaby.utils.FileUtil;

import java.io.File;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ZModrinthIntegration extends ModrinthIntegration {

    private ZLogger logger;
    private String pathString;
    private String id;
    private String v;
    private Path instancePath;
    private Path cachePath;
    private Path modsPath;

    public ZModrinthIntegration(ZLogger logger, String id, String v) throws Exception {
        super(logger, Path.of(Application.getInstancePath()+"instances/modrinth-"+id.toLowerCase()+"-"+v.toLowerCase()+"/cache/"));
        this.logger = logger;
        this.id = id;
        this.v = v;
        pathString = Application.getInstancePath()+"instances/modrinth-"+id.toLowerCase()+"-"+v.toLowerCase()+"/";

        instancePath = Path.of(pathString);
        cachePath = Path.of(pathString+"cache/");
        modsPath = Path.of(pathString+"mods/");
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

    public String getID() {
        return id;
    }

    public ZLogger getLogger() {
        return logger;
    }

    public void install(String version) {
        logger.log("[MODRINTH] (INTEGRATION) Starting installation of modrinth modpack "+id+"...");
        CompletableFuture.runAsync(()->{
            try {
                logger.log("[MODRINTH] (INTEGRATION) Getting modpack info for version "+version+"...");
                ModrinthModPackInfo info = new ModrinthModPackInfo(id,version,true);
                logger.log("[MODRINTH] (INTEGRATION) Resolving "+id+"-"+version+"...");
                ModrinthModPack pack = getModrinthModPack(info);
                String packName = pack.getName();
                String packVersion = pack.getVersion();
                logger.log("[MODRINTH] (INTEGRATION) Resolved modpack "+packName+" v"+packVersion+"!");
                List<Mod> packFiles = pack.getMods();
                List<Mod> packBuiltIns = pack.getBuiltInMods();
                logger.log("[MODRINTH] (INTEGRATION) Starting download of "+packFiles.size()+" files...");
                downloadFiles(packFiles);
                logger.debug(" ");
                logger.log("[MODRINTH] (INTEGRATION) Verifying "+packBuiltIns.size()+" built in files...");
                logger.log("[MODRINTH] (INTEGRATION) Building zyneonInstance file from modrinth data...");
                Config instance = new Config(pathString+"zyneonInstance.json");
                instance.set("modpack.id","modrinth-"+id.toLowerCase()+"-"+v.toLowerCase());
                instance.set("modpack.name",packName);
                instance.set("modpack.version",packVersion);
                logger.log("[MODRINTH] (INTEGRATION) Gathering modloader and Minecraft infos...");
                Config modrinth = new Config(new File(pathString+"modrinth.index.json"));
                String modloader = "Vanilla";
                String mlversion = "No mods";
                String minecraft = modrinth.getString("dependencies.minecraft");
                if(modrinth.getString("dependencies.forge")!=null) {
                    modloader = "Forge";
                    mlversion = modrinth.getString("dependencies.forge");
                    instance.set("modpack.forge.version",mlversion);
                    instance.set("modpack.forge.type", MinecraftVersion.getForgeType(minecraft));
                } else if(modrinth.getString("dependencies.fabric-loader")!=null) {
                    modloader = "Fabric";
                    mlversion = modrinth.getString("dependencies.fabric-loader");
                    instance.set("modpack.fabric",mlversion);
                }
                logger.log("[MODRINTH] (INTEGRATION) Found "+modloader+" ("+mlversion+") for Minecraft "+minecraft+"!");
                instance.set("modpack.minecraft",minecraft);
                instance.set("modpack.instance","instances/modrinth-"+id.toLowerCase()+"-"+v.toLowerCase());
                String description = "This is a modpack instance downloaded from modrinth!";
                if(modrinth.getString("summary")!=null) {
                    description = modrinth.getString("summary");
                }
                instance.set("modpack.description",description);
                try {
                    File icon = new File(pathString + "icon.png");
                    if (icon.exists()) {
                        instance.set("modpack.icon", URLDecoder.decode(icon.getAbsolutePath(), StandardCharsets.UTF_8).replace("\\","/"));
                    }
                    File logo = new File(pathString + "instance.png");
                    if (logo.exists()) {
                        instance.set("modpack.logo", URLDecoder.decode(logo.getAbsolutePath(), StandardCharsets.UTF_8).replace("\\","/"));
                    }
                } catch (Exception ignore) {}
                logger.log("[MODRINTH] (INTEGRATION) Successfully built zyneonInstance file!");
                logger.log("[MODRINTH] (INTEGRATION) Installed modrinth modpack "+packName+" v"+packVersion+"!");
                Application.loadInstances();
                Application.getFrame().getBrowser().loadURL(Application.getInstancesURL()+"?tab=modrinth-"+id.toLowerCase()+"-"+v.toLowerCase());
            } catch (Exception e) {
                logger.error("[MODRINTH] (INTEGRATION) Couldn't initialise modrinth modpack: "+e.getMessage());
                Application.getFrame().getBrowser().loadURL(Application.getInstancesURL());
            }
        });
    }

    private void downloadFiles(List<Mod> files) {
        for(Mod file:files) {
            String name = file.getName();
            String url = file.getDownloadURL();
            String sha1 = file.getSha1();
            long size = file.getSize();
            logger.debug(" ");
            logger.debug("[MODRINTH] (INTEGRATION) Preparing "+name+" from: "+url+"...");
            logger.debug("[MODRINTH] (INTEGRATION) Data: "+sha1+" ("+size+")");
            if(name.contains("/")) {
                logger.debug("[MODRINTH] (INTEGRATION) Created destination folder: "+new File(instancePath.toString()+"/"+name).getParentFile().mkdirs());
                logger.debug("[MODRINTH] (INTEGRATION) Downloading...");
                try {
                    logger.debug("[MODRINTH] (INTEGRATION) Downloaded "+FileUtil.downloadFile(url, instancePath.toString() +"/"+ name).getAbsolutePath()+"!");
                } catch (Exception e) {
                    logger.error("[MODRINTH] (INTEGRATION) Couldn't download "+name+" from "+url+": "+e.getMessage());
                }
            } else {
                logger.debug("[MODRINTH] (INTEGRATION) Created destination folder: " + new File(modsPath.toString() +"/"+ name).getParentFile().mkdirs());
                logger.debug("[MODRINTH] (INTEGRATION) Downloading...");
                try {
                    logger.debug("[MODRINTH] (INTEGRATION) Downloaded "+FileUtil.downloadFile(url, modsPath.toString() +"/"+ name));
                } catch (Exception e) {
                    logger.error("[MODRINTH] (INTEGRATION) Couldn't download " + name + " from " + url + ": " + e.getMessage());
                }
            }
        }
    }
}