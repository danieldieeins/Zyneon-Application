package live.nerotv.zyneon.app.backend.modpack;

import fr.flowarg.flowupdater.versions.ForgeVersionType;

import java.net.URL;

public class ForgePack extends Modpack{

    private String forgeVersion;
    private ForgeVersionType forgeType;

    public ForgePack(URL fileDownload) {
        super(fileDownload);
    }

    public URL getMods() {
        return null;
    }

    public ForgeVersionType getForgeType() {
        return ForgeVersionType.NEW;
    }

    public String getForgeVersion() {
        return forgeVersion;
    }
}
