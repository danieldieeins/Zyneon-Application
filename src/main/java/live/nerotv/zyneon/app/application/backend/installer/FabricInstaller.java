package live.nerotv.zyneon.app.application.backend.installer;

import fr.flowarg.flowupdater.FlowUpdater;
import fr.flowarg.flowupdater.utils.UpdaterOptions;
import fr.flowarg.flowupdater.versions.FabricVersion;
import fr.flowarg.flowupdater.versions.VanillaVersion;
import live.nerotv.Main;
import live.nerotv.zyneon.app.application.backend.instance.FabricInstance;

import java.nio.file.Path;

public class FabricInstaller {

    public boolean downloadInstance(FabricInstance instance) {
        String minecraftVersion = instance.getMinecraftVersion();
        String fabricVersion = instance.getFabricVersion();
        Path instancePath = Path.of(instance.getPath());
        //URL JSON = null;

        VanillaVersion vanillaVersion = new VanillaVersion.VanillaVersionBuilder()
                .withName(minecraftVersion)
                .build();

        UpdaterOptions options = new UpdaterOptions.UpdaterOptionsBuilder()
                .build();

        //List<CurseFileInfo> curseMods = CurseFileInfo.getFilesFromJson(JSON);
        //List<Mod> mods = Mod.getModsFromJson(JSON);

        FabricVersion fabric = new FabricVersion.FabricVersionBuilder()
                .withFabricVersion(fabricVersion)
                //.withCurseMods(JSON)
                //.withMods(JSON)
                .build();

        FlowUpdater updater = new FlowUpdater.FlowUpdaterBuilder()
                .withVanillaVersion(vanillaVersion)
                .withModLoaderVersion(fabric)
                .withUpdaterOptions(options)
                .build();

        try {
            updater.update(instancePath);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean download(String minecraftVersion, String fabricVersion, Path instancePath) {
        VanillaVersion vanillaVersion = new VanillaVersion.VanillaVersionBuilder()
                .withName(minecraftVersion)
                .build();

        UpdaterOptions options = new UpdaterOptions.UpdaterOptionsBuilder()
                .build();

        FabricVersion fabric = new FabricVersion.FabricVersionBuilder()
                .withFabricVersion(fabricVersion)
                .build();

        FlowUpdater updater = new FlowUpdater.FlowUpdaterBuilder()
                .withVanillaVersion(vanillaVersion)
                .withModLoaderVersion(fabric)
                .withUpdaterOptions(options)
                .build();

        try {
            updater.update(instancePath);
            return true;
        } catch (Exception e) {
            Main.getLogger().error("Error: Couldn't download Minecraft " + minecraftVersion + " with Fabric " + fabricVersion);
            return false;
        }
    }
}