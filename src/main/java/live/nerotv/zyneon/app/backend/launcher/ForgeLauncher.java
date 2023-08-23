package live.nerotv.zyneon.app.backend.launcher;

import fr.flowarg.flowupdater.versions.ForgeVersionType;
import fr.flowarg.openlauncherlib.NoFramework;
import fr.theshark34.openlauncherlib.minecraft.GameFolder;
import javafx.application.Platform;
import live.nerotv.Main;
import live.nerotv.zyneon.app.backend.installer.ForgeInstaller;
import live.nerotv.zyneon.app.backend.modpack.ForgePack;

import java.nio.file.Path;

public class ForgeLauncher {

    public boolean launch(ForgePack modpack, int ram) {
        if(Main.config.get("settings.memory."+modpack.getID())!=null) {
            ram = (int)Main.config.get("settings.memory."+modpack.getID());
        }
        return launch(modpack.getMinecraftVersion(), modpack.getForgeVersion(), modpack.getForgeType(), ram, modpack.getPath());
    }

    public boolean launch(String minecraftVersion, String forgeVersion, ForgeVersionType forgeType, int ram, Path instancePath) {
        if(ram<1024) {
            ram = 1024;
        }
        if(new ForgeInstaller().download(minecraftVersion,forgeVersion,forgeType,instancePath)) {
            NoFramework framework = new NoFramework(
                    instancePath,
                    Main.auth.getAuthInfos(),
                    GameFolder.FLOW_UPDATER
            );
            framework.getAdditionalVmArgs().add("-Xmx" + ram + "M");
            try {
                Process p = framework.launch(minecraftVersion, forgeVersion, NoFramework.ModLoader.FORGE);
                Main.frame.getBrowser().executeJavaScript("javascript:OpenModal('run')","https://a.nerotv.live/zyneon/application/html/account.html",5);
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