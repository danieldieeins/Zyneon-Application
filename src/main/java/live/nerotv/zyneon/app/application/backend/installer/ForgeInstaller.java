package live.nerotv.zyneon.app.application.backend.installer;

import fr.flowarg.flowupdater.FlowUpdater;
import fr.flowarg.flowupdater.utils.UpdaterOptions;
import fr.flowarg.flowupdater.versions.AbstractForgeVersion;
import fr.flowarg.flowupdater.versions.ForgeVersionBuilder;
import fr.flowarg.flowupdater.versions.ForgeVersionType;
import fr.flowarg.flowupdater.versions.VanillaVersion;
import live.nerotv.Main;
import live.nerotv.zyneon.app.application.backend.instance.ForgeInstance;

import java.nio.file.Path;

public class ForgeInstaller {

    public boolean downloadInstance(ForgeInstance instance) {
        String minecraftVersion = instance.getMinecraftVersion();
        String forgeVersion = instance.getForgeVersion();
        ForgeVersionType type = instance.getForgeType();
        Path instancePath = Path.of(instance.getPath());
        //URL JSON = null;

        VanillaVersion vanillaVersion = new VanillaVersion.VanillaVersionBuilder()
                .withName(minecraftVersion)
                .build();

        UpdaterOptions options = new UpdaterOptions.UpdaterOptionsBuilder()
                .build();

        //List<CurseFileInfo> curseMods = CurseFileInfo.getFilesFromJson(JSON);
        //List<Mod> mods = Mod.getModsFromJson(JSON);

        AbstractForgeVersion forge = new ForgeVersionBuilder(type)
                .withForgeVersion(minecraftVersion+"-"+forgeVersion)
                //.withCurseMods(JSON)
                //.withMods(JSON)
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
            return false;
        }
    }

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