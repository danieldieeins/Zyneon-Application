package live.nerotv.zyneon.app.backend.installer;

import fr.flowarg.flowupdater.FlowUpdater;
import fr.flowarg.flowupdater.download.json.CurseFileInfo;
import fr.flowarg.flowupdater.download.json.Mod;
import fr.flowarg.flowupdater.utils.UpdaterOptions;
import fr.flowarg.flowupdater.versions.FabricVersion;
import fr.flowarg.flowupdater.versions.VanillaVersion;
import live.nerotv.zyneon.app.backend.modpack.FabricPack;

import java.net.URL;
import java.nio.file.Path;
import java.util.List;

public class FabricInstaller {

    public boolean downloadModpack(FabricPack modpack) {
        String minecraftVersion = modpack.getMinecraftVersion();
        String fabricVersion = modpack.getFabricVersion();
        Path instancePath = modpack.getPath();
        URL JSON = modpack.getMods();

        VanillaVersion vanillaVersion = new VanillaVersion.VanillaVersionBuilder()
                .withName(minecraftVersion)
                .build();

        UpdaterOptions options = new UpdaterOptions.UpdaterOptionsBuilder()
                .build();

        List<CurseFileInfo> curseMods = CurseFileInfo.getFilesFromJson(JSON);
        List<Mod> mods = Mod.getModsFromJson(JSON);

        FabricVersion fabric = new FabricVersion.FabricVersionBuilder()
                .withFabricVersion(fabricVersion)
                .withCurseMods(JSON)
                .withMods(JSON)
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
            return false;
        }
    }
}