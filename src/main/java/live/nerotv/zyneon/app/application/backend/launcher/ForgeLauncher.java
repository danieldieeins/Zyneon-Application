package live.nerotv.zyneon.app.application.backend.launcher;

import fr.flowarg.flowupdater.versions.ForgeVersionType;
import fr.flowarg.openlauncherlib.NoFramework;
import fr.theshark34.openlauncherlib.JavaUtil;
import fr.theshark34.openlauncherlib.minecraft.GameFolder;
import live.nerotv.Main;
import live.nerotv.zyneon.app.application.Application;
import live.nerotv.zyneon.app.application.backend.framework.MinecraftVersion;
import live.nerotv.zyneon.app.application.backend.installer.ForgeInstaller;
import live.nerotv.zyneon.app.application.backend.instance.ForgeInstance;
import live.nerotv.zyneon.app.application.backend.utils.frame.ZyneonWebFrame;

import java.io.File;
import java.nio.file.Path;

public class ForgeLauncher {

    private final ZyneonWebFrame frame;

    public ForgeLauncher(ZyneonWebFrame frame) {
        this.frame = frame;
    }

    public void launch(ForgeInstance instance, int ram) {
        String id = instance.getID();
        String ramID = id.replace(".","").replace("/","");
        if(Main.config.get("settings.memory."+ramID)!=null) {
            ram = Main.config.getInteger("settings.memory."+ramID);
        }
        if(!new File(instance.getPath()+"/pack.zip").exists()) {
            frame.getBrowser().executeJavaScript("javascript:OpenModal('installing')","https://a.nerotv.live/zyneon/application/html/account.html",5);
            instance.update();
        }
        if(!instance.checkVersion()) {
            frame.getBrowser().executeJavaScript("javascript:OpenModal('installing')","https://a.nerotv.live/zyneon/application/html/account.html",5);
            instance.update();
        }
        launch(instance.getMinecraftVersion(), instance.getForgeVersion(), instance.getForgeType(), ram, Path.of(instance.getPath()));
    }

    public void launch(String minecraftVersion, String forgeVersion, ForgeVersionType forgeType, int ram, Path instancePath) {
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
                Process p = framework.launch(minecraftVersion, forgeVersion, forge);
            } catch (Exception e) {
                Main.getLogger().error("Couldn't start: "+e.getMessage());
                throw new RuntimeException(e);
            }
        } else {
            Main.getLogger().error("Error: couldn't start Forge "+forgeVersion+" ("+forgeType+") for Minecraft "+minecraftVersion+" in "+instancePath+" with "+ram+"M RAM");
        }
    }
}