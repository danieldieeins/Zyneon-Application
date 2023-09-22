package live.nerotv.zyneon.app.application.backend.launcher;

import fr.flowarg.openlauncherlib.NoFramework;
import fr.theshark34.openlauncherlib.JavaUtil;
import fr.theshark34.openlauncherlib.minecraft.GameFolder;
import javafx.application.Platform;
import live.nerotv.Main;
import live.nerotv.openlauncherapi.auth.SimpleMicrosoftAuth;
import live.nerotv.zyneon.app.application.backend.installer.FabricInstaller;
import live.nerotv.zyneon.app.application.backend.instance.FabricInstance;
import live.nerotv.zyneon.app.application.backend.utils.frame.ZyneonWebFrame;

import java.io.File;
import java.nio.file.Path;

public class FabricLauncher {

    private ZyneonWebFrame frame;
    private SimpleMicrosoftAuth auth;

    public FabricLauncher(SimpleMicrosoftAuth auth, ZyneonWebFrame frame) {
        this.auth = auth;
        this.frame = frame;
    }

    public boolean launch(FabricInstance instance, int ram) {
        String id = instance.getID().replace("/","").replace(".","");
        if(Main.config.get("settings.memory."+id)!=null) {
            System.out.println("settings.memory."+id+", "+Main.config.get("settings.memory."+id));
            ram = Main.config.getInteger("settings.memory."+id);
        }
        if(!new File(instance.getPath()+"/pack.zip").exists()) {
            frame.getBrowser().executeJavaScript("javascript:OpenModal('install')","https://a.nerotv.live/zyneon/application/html/account.html",5);
            instance.update();
        }
        if(!instance.checkVersion()) {
            frame.getBrowser().executeJavaScript("javascript:OpenModal('install')","https://a.nerotv.live/zyneon/application/html/account.html",5);
            instance.update();
        }
        return launch(instance.getMinecraftVersion(), instance.getFabricVersion(), ram, Path.of(instance.getPath()));
    }

    public boolean launch(String minecraftVersion, String fabricVersion, int ram, Path instancePath) {
        if(MinecraftVersion.getType(minecraftVersion)!=null) {
            MinecraftVersion.Type type = MinecraftVersion.getType(minecraftVersion);
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
        if(ram<1024) {
            ram = 1024;
        }
        if(new FabricInstaller().download(minecraftVersion,fabricVersion,instancePath)) {
            NoFramework framework = new NoFramework(
                    instancePath,
                    auth.getAuthInfos(),
                    GameFolder.FLOW_UPDATER
            );
            framework.getAdditionalVmArgs().add("-Xmx" + ram + "M");
            try {
                Process p = framework.launch(minecraftVersion, fabricVersion, NoFramework.ModLoader.FABRIC);
                frame.getBrowser().executeJavaScript("javascript:OpenModal('run')","https://a.nerotv.live/zyneon/application/html/account.html",5);
                Platform.runLater(() -> {
                    try {
                        p.waitFor();
                        Platform.exit();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                });

                return true;
            } catch (Exception e) {
                System.out.println("Error: couldn't start Fabric "+fabricVersion+" for Minecraft "+minecraftVersion+" in "+instancePath+" with "+ram+"M RAM");
                return false;
            }
        } else {
            System.out.println("Error: couldn't start Fabric "+fabricVersion+" for Minecraft "+minecraftVersion+" in "+instancePath+" with "+ram+"M RAM");
            return false;
        }
    }
}