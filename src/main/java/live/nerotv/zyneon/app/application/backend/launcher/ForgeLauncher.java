package live.nerotv.zyneon.app.application.backend.launcher;

import fr.flowarg.flowupdater.versions.ForgeVersionType;
import fr.flowarg.openlauncherlib.NoFramework;
import fr.theshark34.openlauncherlib.minecraft.GameFolder;
import javafx.application.Platform;
import live.nerotv.Main;
import live.nerotv.openlauncherapi.auth.SimpleMicrosoftAuth;
import live.nerotv.zyneon.app.application.backend.installer.ForgeInstaller;
import live.nerotv.zyneon.app.application.backend.instance.ForgeInstance;
import live.nerotv.zyneon.app.application.backend.utils.frame.ZyneonWebFrame;

import java.io.File;
import java.nio.file.Path;

public class ForgeLauncher {

    private ZyneonWebFrame frame;
    private SimpleMicrosoftAuth auth;

    public ForgeLauncher(SimpleMicrosoftAuth auth, ZyneonWebFrame frame) {
        this.auth = auth;
        this.frame = frame;
    }

    public boolean launch(ForgeInstance instance, int ram) {
        if(Main.config.get("settings.memory."+instance.getID())!=null) {
            ram = Main.config.getInteger("settings.memory."+instance.getID());
        }
        if(!new File(instance.getPath()+"/pack.zip").exists()) {
            frame.getBrowser().executeJavaScript("javascript:OpenModal('install')","https://a.nerotv.live/zyneon/application/html/account.html",5);
            instance.update();
        }
        if(!instance.checkVersion()) {
            frame.getBrowser().executeJavaScript("javascript:OpenModal('install')","https://a.nerotv.live/zyneon/application/html/account.html",5);
            instance.update();
        }
        return launch(instance.getMinecraftVersion(), instance.getForgeVersion(), instance.getForgeType(), ram, Path.of(instance.getPath()));
    }

    public boolean launch(String minecraftVersion, String forgeVersion, ForgeVersionType forgeType, int ram, Path instancePath) {
        if(ram<1024) {
            ram = 1024;
        }
        if(new ForgeInstaller().download(minecraftVersion,forgeVersion,forgeType,instancePath)) {
            NoFramework framework = new NoFramework(
                    instancePath,
                    auth.getAuthInfos(),
                    GameFolder.FLOW_UPDATER
            );
            framework.getAdditionalVmArgs().add("-Xmx" + ram + "M");
            try {
                Process p = framework.launch(minecraftVersion, forgeVersion, NoFramework.ModLoader.FORGE);
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
                System.out.println("Error: couldn't start Forge "+forgeVersion+" ("+forgeType+") for Minecraft "+minecraftVersion+" in "+instancePath+" with "+ram+"M RAM");
                return false;
            }
        } else {
            System.out.println("Error: couldn't start Forge "+forgeVersion+" ("+forgeType+") for Minecraft "+minecraftVersion+" in "+instancePath+" with "+ram+"M RAM");
            return false;
        }
    }

}