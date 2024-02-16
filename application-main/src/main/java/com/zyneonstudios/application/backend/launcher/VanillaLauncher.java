package com.zyneonstudios.application.backend.launcher;

import com.zyneonstudios.Main;
import com.zyneonstudios.application.Application;
import com.zyneonstudios.application.backend.installer.VanillaInstaller;
import com.zyneonstudios.application.backend.instance.Instance;
import com.zyneonstudios.application.backend.utils.backend.MinecraftVersion;
import com.zyneonstudios.application.backend.utils.frame.LogFrame;
import com.zyneonstudios.application.backend.utils.frame.ZyneonWebFrame;
import fr.flowarg.openlauncherlib.NoFramework;
import fr.theshark34.openlauncherlib.JavaUtil;
import fr.theshark34.openlauncherlib.minecraft.GameFolder;

import javax.swing.*;
import java.io.File;
import java.nio.file.Path;

public class VanillaLauncher {

    private final ZyneonWebFrame frame;

    public VanillaLauncher(ZyneonWebFrame frame) {
        this.frame = frame;
    }

    public void launch(Instance instance, int ram, boolean log) {
        if(instance.getSettings().get("configuration.ram")!=null) {
            ram = instance.getSettings().getInteger("configuration.ram");
        }
        if(!new File(instance.getPath()+"/pack.zip").exists()) {
            instance.update();
        }
        if(!instance.checkVersion()) {
            instance.update();
        }
        launch(instance.getMinecraftVersion(), ram, Path.of(instance.getPath()),log);
    }

    public void launch(String version, int ram, Path instancePath, boolean enableLogOutput) {
        MinecraftVersion.Type type = MinecraftVersion.getType(version);
        if(type!=null) {
            if(type.equals(MinecraftVersion.Type.LEGACY)) {
                JavaUtil.setJavaCommand(null);
                System.setProperty("java.home", Main.getDirectoryPath()+"libs/jre-8");
            } else if(type.equals(MinecraftVersion.Type.SEMI_NEW)) {
                JavaUtil.setJavaCommand(null);
                System.setProperty("java.home", Main.getDirectoryPath()+"libs/jre-11");
            } else if(type.equals(MinecraftVersion.Type.NEW)) {
                JavaUtil.setJavaCommand(null);
                System.setProperty("java.home", Main.getDirectoryPath()+"libs/jre");
            }
        }
        if(ram<512) {
            ram = 512;
        }
        if(new VanillaInstaller().download(version,instancePath)) {
            NoFramework framework = new NoFramework(
                    instancePath,
                    Application.auth.getAuthInfos(),
                    GameFolder.FLOW_UPDATER
            );
            framework.getAdditionalVmArgs().add("-Xms512M");
            framework.getAdditionalVmArgs().add("-Xmx4096M");
            try {
                Process game = framework.launch(version, "", NoFramework.ModLoader.VANILLA);
                frame.executeJavaScript("launchStarted();");
                Application.getFrame().setState(JFrame.ICONIFIED);
                LogFrame log;
                if(enableLogOutput) {
                    log = new LogFrame(game.getInputStream(),"Minecraft "+version);
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
                Main.getLogger().error("[LAUNCHER] Couldn't start Minecraft Vanilla " + version + " in " + instancePath + " with " + ram + "M RAM");
                throw new RuntimeException(e);
            }
        } else {
            frame.executeJavaScript("launchDefault();");
            Main.getLogger().error("[LAUNCHER] Couldn't start Minecraft Vanilla " + version + " in " + instancePath + " with " + ram + "M RAM");
        }
    }
}