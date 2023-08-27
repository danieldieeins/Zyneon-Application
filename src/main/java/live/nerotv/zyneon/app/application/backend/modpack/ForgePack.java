package live.nerotv.zyneon.app.application.backend.modpack;

import fr.flowarg.flowupdater.versions.ForgeVersionType;
import java.net.URL;

public class ForgePack extends Modpack{

    private String forgeVersion;
    private ForgeVersionType forgeType;

    public ForgePack(String fileDownload) {
        super(fileDownload);
        forgeVersion = (String)getConfig().get("modpack.forge.version");
        String type = (String)getConfig().get("modpack.forge.type");
        if(type==null||forgeVersion==null) {
            throw new NullPointerException("Modpack file doesn't contain all values");
        }
        if(type.toLowerCase().contains("neo")) {
            forgeType = ForgeVersionType.NEO_FORGE;
        } else if (type.contains("old")) {
            forgeType = ForgeVersionType.OLD;
        } else {
            forgeType = ForgeVersionType.NEW;
        }
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

    public void unloadPack() {
        forgeVersion = null;
        forgeType = null;
        unload();
    }
}
