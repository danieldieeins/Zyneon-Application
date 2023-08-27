package live.nerotv.zyneon.app.application.backend.installer;

import fr.flowarg.flowupdater.FlowUpdater;
import fr.flowarg.flowupdater.download.json.CurseFileInfo;
import fr.flowarg.flowupdater.download.json.Mod;
import fr.flowarg.flowupdater.utils.UpdaterOptions;
import fr.flowarg.flowupdater.versions.AbstractForgeVersion;
import fr.flowarg.flowupdater.versions.ForgeVersionBuilder;
import fr.flowarg.flowupdater.versions.ForgeVersionType;
import fr.flowarg.flowupdater.versions.VanillaVersion;
import live.nerotv.zyneon.app.application.backend.modpack.ForgePack;
import java.net.URL;
import java.nio.file.Path;
import java.util.List;

public class ForgeInstaller {

    public boolean downloadModpack(ForgePack modpack) {
        String minecraftVersion = modpack.getMinecraftVersion();
        String forgeVersion = modpack.getForgeVersion();
        ForgeVersionType type = modpack.getForgeType();
        Path instancePath = modpack.getPath();
        URL JSON = modpack.getMods();

        VanillaVersion vanillaVersion = new VanillaVersion.VanillaVersionBuilder()
                .withName(minecraftVersion)
                .build();

        UpdaterOptions options = new UpdaterOptions.UpdaterOptionsBuilder()
                .build();

        List<CurseFileInfo> curseMods = CurseFileInfo.getFilesFromJson(JSON);
        List<Mod> mods = Mod.getModsFromJson(JSON);

        AbstractForgeVersion forge = new ForgeVersionBuilder(type)
                .withForgeVersion(minecraftVersion+"-"+forgeVersion)
                .withCurseMods(JSON)
                .withMods(JSON)
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
            System.out.println("Error: Couldn't download Minecraft "+minecraftVersion+" with Forge "+forgeVersion);
            return false;
        }
    }
}