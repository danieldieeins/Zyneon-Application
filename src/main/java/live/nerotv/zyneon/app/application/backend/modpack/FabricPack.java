package live.nerotv.zyneon.app.application.backend.modpack;

import java.net.URL;

public class FabricPack extends Modpack {

    private String fabricVersion;

    public FabricPack(String fileDownload) {
        super(fileDownload);
        fabricVersion = (String)getConfig().get("modpack.fabric");
        if(fabricVersion==null) {
            throw new NullPointerException("Modpack file doesn't contain all values");
        }
    }

    public URL getMods() {
        return null;
    }

    public String getFabricVersion() {
        return fabricVersion;
    }

    public void unloadPack() {
        fabricVersion = null;
        unload();
    }
}