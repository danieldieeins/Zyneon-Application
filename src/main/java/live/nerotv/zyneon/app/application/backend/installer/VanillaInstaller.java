package live.nerotv.zyneon.app.application.backend.installer;

import fr.flowarg.flowupdater.FlowUpdater;
import fr.flowarg.flowupdater.versions.VanillaVersion;
import live.nerotv.Main;
import live.nerotv.zyneon.app.application.backend.instance.Instance;

import java.nio.file.Path;

public class VanillaInstaller {

    public boolean downloadInstance(Instance instance) {
        return download(instance.getMinecraftVersion(),Path.of(instance.getPath()));
    }

    public boolean download(String version, Path instancePath) {
        Main.getLogger().debug("Starting download of Minecraft "+version);
        VanillaVersion vanilla = new VanillaVersion.VanillaVersionBuilder()
                .withName(version)
                .build();
        FlowUpdater flowUpdater = new FlowUpdater.FlowUpdaterBuilder()
                .withVanillaVersion(vanilla)
                .build();
        try {
            flowUpdater.update(instancePath);
            Main.getLogger().debug("Successfully downloaded Minecraft "+version);
            return true;
        } catch (Exception e) {
            Main.getLogger().error("Error: Couldn't download Minecraft "+version);
            return false;
        }
    }
}