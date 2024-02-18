package com.zyneonstudios.application.backend.launcher;

import com.zyneonstudios.Main;
import com.zyneonstudios.application.Application;
import com.zyneonstudios.application.backend.installer.ForgeInstaller;
import com.zyneonstudios.application.backend.instance.ForgeInstance;
import com.zyneonstudios.application.backend.utils.backend.MinecraftVersion;
import com.zyneonstudios.application.backend.utils.frame.LogFrame;
import com.zyneonstudios.application.backend.utils.frame.web.ZyneonWebFrame;
import fr.flowarg.flowupdater.versions.ForgeVersionType;
import fr.flowarg.openlauncherlib.NoFramework;
import fr.theshark34.openlauncherlib.minecraft.GameFolder;

import javax.swing.*;
import java.io.File;
import java.nio.file.Path;

public class ForgeLauncher {

    private final ZyneonWebFrame frame;

    public ForgeLauncher(ZyneonWebFrame frame) {
        this.frame = frame;
    }

    public void launch(ForgeInstance instance, int ram, boolean log) {
        if(instance.getSettings().get("configuration.ram")!=null) {
            ram = instance.getSettings().getInteger("configuration.ram");
        }
        if(!new File(instance.getPath()+"/pack.zip").exists()) {
            instance.update();
        }
        if(!instance.checkVersion()) {
            instance.update();
        }
        launch(instance.getMinecraftVersion(), instance.getForgeVersion(), instance.getForgeType(), ram, Path.of(instance.getPath()),log);
    }

    public void launch(String minecraftVersion, String forgeVersion, ForgeVersionType forgeType, int ram, Path instancePath, boolean enableLogOutput) {
        MinecraftVersion.Type type = MinecraftVersion.getType(minecraftVersion);
        if(type!=null) {
            Launcher.setJava(type);
        }
        if(ram<512) {
            ram = 512;
        }
        if(new ForgeInstaller().download(minecraftVersion,forgeVersion,forgeType,instancePath)) {
            NoFramework.ModLoader forge;
            if(forgeType==ForgeVersionType.OLD) {
                forge = NoFramework.ModLoader.OLD_FORGE;
            } else if(forgeType==ForgeVersionType.NEO_FORGE) {
                forge = NoFramework.ModLoader.NEO_FORGE;
            } else {
                forge = NoFramework.ModLoader.FORGE;
            }
            NoFramework framework = new NoFramework(
                    instancePath,
                    Application.auth.getAuthInfos(),
                    GameFolder.FLOW_UPDATER
            );
            if(minecraftVersion.equals("1.7.10")) {
                framework.setCustomModLoaderJsonFileName("1.7.10-Forge"+forgeVersion+".json");
            }
            framework.getAdditionalVmArgs().add("-Xms512M");
            framework.getAdditionalVmArgs().add("-Xmx" + ram + "M");
            try {
                Process game = framework.launch(minecraftVersion, forgeVersion, forge);
                frame.executeJavaScript("launchStarted();");
                Application.getFrame().setState(JFrame.ICONIFIED);
                LogFrame log;
                if(enableLogOutput) {
                    log = new LogFrame(game.getInputStream(),"Minecraft "+minecraftVersion+" (with "+forgeType.toString().toLowerCase()+"Forge "+forgeVersion+")");
                } else {
                    log = null;
                }
                game.onExit().thenRun(()->{
                    if(log!=null) {
                        log.onStop();
                    }
                    Application.getFrame().setState(JFrame.NORMAL);
                    frame.executeJavaScript("launchDefault();");
                });
            } catch (Exception e) {
                frame.executeJavaScript("launchDefault();");
                if(!Application.auth.isLoggedIn()) {
                    frame.getBrowser().loadURL(Application.getSettingsURL()+"?tab=profile");
                }
                Main.getLogger().error("[LAUNCHER] Couldn't start Forge "+forgeVersion+" ("+forgeType+") for Minecraft "+minecraftVersion+" in "+instancePath+" with "+ram+"M RAM");
                throw new RuntimeException(e);
            }
        } else {
            frame.executeJavaScript("launchDefault();");
            Main.getLogger().error("[LAUNCHER] Couldn't start Forge "+forgeVersion+" ("+forgeType+") for Minecraft "+minecraftVersion+" in "+instancePath+" with "+ram+"M RAM");
        }
    }
}