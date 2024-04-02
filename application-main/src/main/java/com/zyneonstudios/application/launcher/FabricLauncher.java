package com.zyneonstudios.application.launcher;

import com.zyneonstudios.Main;
import com.zyneonstudios.application.Application;
import com.zyneonstudios.application.installer.FabricInstaller;
import com.zyneonstudios.application.installer.java.OperatingSystem;
import com.zyneonstudios.application.instance.FabricInstance;
import com.zyneonstudios.application.utils.backend.MinecraftVersion;
import com.zyneonstudios.application.utils.frame.LogFrame;
import com.zyneonstudios.application.utils.frame.web.ZyneonWebFrame;
import fr.flowarg.openlauncherlib.NoFramework;
import fr.theshark34.openlauncherlib.minecraft.GameFolder;

import javax.swing.*;
import java.io.File;
import java.nio.file.Path;

public class FabricLauncher {

    private final ZyneonWebFrame frame;

    public FabricLauncher(ZyneonWebFrame frame) {
        this.frame = frame;
    }

    public void launch(FabricInstance instance, int ram, boolean log) {
        if(instance.getSettings().get("configuration.ram")!=null) {
            ram = instance.getSettings().getInteger("configuration.ram");
        }
        if(!new File(instance.getPath()+"/pack.zip").exists()) {
            instance.update();
        }
        if(!instance.checkVersion()) {
            instance.update();
        }
        launch(instance.getMinecraftVersion(), instance.getFabricVersion(), ram, Path.of(instance.getPath()),log);
    }

    public void launch(String minecraftVersion, String fabricVersion, int ram, Path instancePath, boolean enableLogOutput) {
        MinecraftVersion.Type type = MinecraftVersion.getType(minecraftVersion);
        if(type!=null) {
            Launcher.setJava(type);
        }
        if(ram<512) {
            ram = 512;
        }
        if(new FabricInstaller().download(minecraftVersion,fabricVersion,instancePath)) {
            NoFramework framework = new NoFramework(
                    instancePath,
                    Application.auth.getAuthInfos(),
                    GameFolder.FLOW_UPDATER
            );
            framework.getAdditionalVmArgs().add("-Xms512M");
            framework.getAdditionalVmArgs().add("-Xmx" + ram + "M");
            if(Main.operatingSystem== OperatingSystem.macOS) {
                framework.getAdditionalVmArgs().add("-XstartOnFirstThread");
            }
            try {
                Process game = framework.launch(minecraftVersion, fabricVersion, NoFramework.ModLoader.FABRIC);
                frame.executeJavaScript("launchStarted();");
                Application.getFrame().setState(JFrame.ICONIFIED);
                LogFrame log;
                if (enableLogOutput) {
                    log = new LogFrame(game.getInputStream(), "Minecraft " + minecraftVersion + " (with Fabric " + fabricVersion + ")");
                } else {
                    log = null;
                }
                game.onExit().thenRun(() -> {
                    if (log != null) {
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
                Main.getLogger().error("[LAUNCHER] Couldn't start Fabric "+fabricVersion+" for Minecraft "+minecraftVersion+" in "+instancePath+" with "+ram+"M RAM.");
                throw new RuntimeException(e);
            }
        } else {
            frame.executeJavaScript("launchDefault();");
            Main.getLogger().error("[LAUNCHER] Couldn't start Fabric "+fabricVersion+" for Minecraft "+minecraftVersion+" in "+instancePath+" with "+ram+"M RAM.");
        }
    }
}