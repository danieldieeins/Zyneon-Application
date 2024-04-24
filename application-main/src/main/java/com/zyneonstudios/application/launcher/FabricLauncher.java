package com.zyneonstudios.application.launcher;

import com.zyneonstudios.Main;
import com.zyneonstudios.application.Application;
import com.zyneonstudios.application.installer.FabricInstaller;
import com.zyneonstudios.application.installer.java.OperatingSystem;
import com.zyneonstudios.application.integrations.index.zyndex.ZyndexIntegration;
import com.zyneonstudios.application.integrations.index.zyndex.instance.ReadableInstance;
import com.zyneonstudios.application.utils.backend.MinecraftVersion;
import com.zyneonstudios.application.utils.frame.LogFrame;
import fr.flowarg.openlauncherlib.NoFramework;
import fr.theshark34.openlauncherlib.minecraft.GameFolder;

import javax.swing.*;
import java.nio.file.Path;

public class FabricLauncher {

    public void launch(ReadableInstance instance) {
        ZyndexIntegration.update(instance);
        launch(instance.getMinecraftVersion(), instance.getFabricVersion(), instance.getSettings().getMemory(), Path.of(instance.getPath()),instance.getId());
    }

    public void launch(String minecraftVersion, String fabricVersion, int ram, Path instancePath,String id) {
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
                Application.getFrame().executeJavaScript("launchStarted();");
                if(!Application.running.contains(id)) {
                    Application.running.add(id);
                }
                Application.getFrame().setState(JFrame.ICONIFIED);
                LogFrame log;
                if (Application.logOutput) {
                    log = new LogFrame(game.getInputStream(), "Minecraft " + minecraftVersion + " (with Fabric " + fabricVersion + ")");
                } else {
                    log = null;
                }
                game.onExit().thenRun(() -> {
                    if (log != null) {
                        log.onStop();
                    }
                    Application.getFrame().setState(JFrame.NORMAL);
                    Application.getFrame().executeJavaScript("launchDefault();");
                    Application.running.remove(id);
                });
            } catch (Exception e) {
                Application.getFrame().executeJavaScript("launchDefault();");
                Application.running.remove(id);
                if(!Application.auth.isLoggedIn()) {
                    Application.getFrame().getBrowser().loadURL(Application.getSettingsURL()+"?tab=profile");
                }
                Main.getLogger().error("[LAUNCHER] Couldn't start Fabric "+fabricVersion+" for Minecraft "+minecraftVersion+" in "+instancePath+" with "+ram+"M RAM.");
                throw new RuntimeException(e);
            }
        } else {
            Application.getFrame().executeJavaScript("launchDefault();");
            Application.running.remove(id);
            Main.getLogger().error("[LAUNCHER] Couldn't start Fabric "+fabricVersion+" for Minecraft "+minecraftVersion+" in "+instancePath+" with "+ram+"M RAM.");
        }
    }
}