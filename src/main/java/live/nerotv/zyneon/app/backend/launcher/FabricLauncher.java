package live.nerotv.zyneon.app.backend.launcher;

import fr.flowarg.openlauncherlib.NoFramework;
import fr.theshark34.openlauncherlib.minecraft.GameFolder;
import javafx.application.Platform;
import live.nerotv.Main;
import live.nerotv.zyneon.app.backend.installer.FabricInstaller;
import live.nerotv.zyneon.app.backend.login.MicrosoftAuth;
import live.nerotv.zyneon.app.backend.modpack.FabricPack;

import java.nio.file.Path;

public class FabricLauncher {

    public boolean launch(FabricPack modpack, int ram) {
        return launch(modpack.getMinecraftVersion(), modpack.getFabricVersion(), ram, modpack.getPath());
    }

    public boolean launch(String minecraftVersion, String fabricVersion, int ram, Path instancePath) {
        if(new FabricInstaller().download(minecraftVersion,fabricVersion,instancePath)) {
            NoFramework framework = new NoFramework(
                    instancePath,
                    MicrosoftAuth.getAuthInfos(),
                    GameFolder.FLOW_UPDATER
            );
            framework.getAdditionalVmArgs().add("-Xmx" + ram + "M");
            try {
                Process p = framework.launch(minecraftVersion, fabricVersion, NoFramework.ModLoader.FABRIC);
                Main.frame.hide();
                Platform.runLater(() -> {
                    try {
                        p.waitFor();
                        Platform.exit();
                        Main.frame.show();
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