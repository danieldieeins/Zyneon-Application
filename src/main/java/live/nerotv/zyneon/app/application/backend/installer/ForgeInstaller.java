package live.nerotv.zyneon.app.application.backend.installer;

import fr.flowarg.flowupdater.FlowUpdater;
import fr.flowarg.flowupdater.utils.UpdaterOptions;
import fr.flowarg.flowupdater.versions.AbstractForgeVersion;
import fr.flowarg.flowupdater.versions.ForgeVersionBuilder;
import fr.flowarg.flowupdater.versions.ForgeVersionType;
import fr.flowarg.flowupdater.versions.VanillaVersion;
import live.nerotv.Main;

import java.nio.file.Path;

public class ForgeInstaller {

    public boolean download(String minecraftVersion, String forgeVersion, ForgeVersionType type, Path instancePath) {
        VanillaVersion vanillaVersion = new VanillaVersion.VanillaVersionBuilder()
                .withName(minecraftVersion)
                .build();

        UpdaterOptions options = new UpdaterOptions.UpdaterOptionsBuilder()
                .build();

        AbstractForgeVersion forge = new ForgeVersionBuilder(type)
                .withForgeVersion(minecraftVersion+"-"+forgeVersion)
                .build();

        FlowUpdater updater = new FlowUpdater.FlowUpdaterBuilder()
                .withVanillaVersion(vanillaVersion)
                .withModLoaderVersion(forge)
                .withUpdaterOptions(options)
                .build();

        try {
            updater.update(instancePath);
            return true;
        } catch (Exception e) {
            Main.getLogger().error("Error: Couldn't download Minecraft " + minecraftVersion + " with Forge " + forgeVersion);
            return false;
        }
    }
}