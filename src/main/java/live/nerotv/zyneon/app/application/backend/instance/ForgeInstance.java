package live.nerotv.zyneon.app.application.backend.instance;

import fr.flowarg.flowupdater.versions.ForgeVersionType;
import live.nerotv.shademebaby.file.Config;

public class ForgeInstance extends VanillaInstance {

    private String forgeVersion;
    private ForgeVersionType forgeType;

    public ForgeInstance(Config json) {
        super(json);
        forgeVersion = json.getString("modpack.forge.version");
        forgeType = ForgeVersionType.valueOf(json.getString("modpack.forge.type"));
    }

    public String getForgeVersion() {
        return forgeVersion;
    }

    public ForgeVersionType getForgeType() {
        return forgeType;
    }

    @Override
    public void unload() {
        forgeType = null;
        forgeVersion = null;
        super.unload();
    }
}