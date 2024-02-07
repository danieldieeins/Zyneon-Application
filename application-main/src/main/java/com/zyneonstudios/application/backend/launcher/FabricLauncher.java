package com.zyneonstudios.application.backend.launcher;

import com.zyneonstudios.Main;
import com.zyneonstudios.application.Application;
import com.zyneonstudios.application.backend.installer.FabricInstaller;
import com.zyneonstudios.application.backend.instance.FabricInstance;
import com.zyneonstudios.application.backend.utils.backend.MinecraftVersion;
import com.zyneonstudios.application.backend.utils.frame.LogFrame;
import com.zyneonstudios.application.backend.utils.frame.ZyneonWebFrame;
import fr.flowarg.openlauncherlib.NoFramework;
import fr.theshark34.openlauncherlib.JavaUtil;
import fr.theshark34.openlauncherlib.minecraft.GameFolder;

import javax.swing.*;
import java.io.File;
import java.nio.file.Path;

public class FabricLauncher {

    private final ZyneonWebFrame frame;

    public FabricLauncher(ZyneonWebFrame frame) {
        this.frame = frame;
    }

    public void launch(FabricInstance instance, int ram) {
        String id = instance.getID();
        String ramID = id.replace(".","").replace("/","");
        if(Application.config.get("settings.memory."+ramID)!=null) {
            ram = Application.config.getInteger("settings.memory."+ramID);
        }
        if(!new File(instance.getPath()+"/pack.zip").exists()) {
            frame.getBrowser().executeJavaScript("javascript:OpenModal('installing')","https://a.nerotv.live/zyneon/application/html/account.html",5);
            instance.update();
        }
        if(!instance.checkVersion()) {
            frame.getBrowser().executeJavaScript("javascript:OpenModal('installing')","https://a.nerotv.live/zyneon/application/html/account.html",5);
            instance.update();
        }
        launch(instance.getMinecraftVersion(), instance.getFabricVersion(), ram, Path.of(instance.getPath()));
    }

    public void launch(String minecraftVersion, String fabricVersion, int ram, Path instancePath) {
        frame.getBrowser().executeJavaScript("javascript:OpenModal('starting')","https://a.nerotv.live/zyneon/application/html/account.html",5);
        MinecraftVersion.Type type = MinecraftVersion.getType(minecraftVersion);
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
        if(new FabricInstaller().download(minecraftVersion,fabricVersion,instancePath)) {
            NoFramework framework = new NoFramework(
                    instancePath,
                    Application.auth.getAuthInfos(),
                    GameFolder.FLOW_UPDATER
            );
            framework.getAdditionalVmArgs().add("-Xms512M");
            framework.getAdditionalVmArgs().add("-Xmx" + ram + "M");
            try {
                Process game = framework.launch(minecraftVersion, fabricVersion, NoFramework.ModLoader.FABRIC);
                LogFrame log = new LogFrame(game.getInputStream(),"Minecraft "+minecraftVersion+" (with Fabric "+fabricVersion+")");
                Application.getFrame().setState(JFrame.ICONIFIED);
                game.onExit().thenRun(()->{
                    Application.getFrame().setState(JFrame.NORMAL);
                    log.onStop();
                });
            } catch (Exception e) {
                Main.getLogger().error("[LAUNCHER] Couldn't start Fabric "+fabricVersion+" for Minecraft "+minecraftVersion+" in "+instancePath+" with "+ram+"M RAM.");
                throw new RuntimeException(e);
            }
        } else {
            Main.getLogger().error("[LAUNCHER] Couldn't start Fabric "+fabricVersion+" for Minecraft "+minecraftVersion+" in "+instancePath+" with "+ram+"M RAM.");
        }
    }
}