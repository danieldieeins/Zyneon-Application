package live.nerotv.zyneon.app.backend.installer;

import fr.flowarg.flowupdater.FlowUpdater;
import fr.flowarg.flowupdater.versions.VanillaVersion;
import live.nerotv.Main;
import live.nerotv.zyneon.app.backend.modpack.Modpack;
import java.nio.file.Path;

public class VanillaInstaller {

    public boolean downloadModpack(Modpack modpack) {
        return download(modpack.getMinecraftVersion(),modpack.getPath());
    }

    public boolean download(String version, Path instancePath) {
        Main.debug("Starting download of Minecraft "+version);
        VanillaVersion vanilla = new VanillaVersion.VanillaVersionBuilder()
                .withName(version)
                .build();
        FlowUpdater flowUpdater = new FlowUpdater.FlowUpdaterBuilder()
                .withVanillaVersion(vanilla)
                .build();
        try {
            flowUpdater.update(instancePath);
            Main.debug("Successfully downloaded Minecraft "+version);
            return true;
        } catch (Exception e) {
            System.out.println("Error: Couldn't download Minecraft "+version);
            return false;
        }
    }
}