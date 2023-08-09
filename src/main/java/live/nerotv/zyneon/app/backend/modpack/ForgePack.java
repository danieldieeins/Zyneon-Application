package live.nerotv.zyneon.app.backend.modpack;

import java.net.URL;

public class ForgePack extends Modpack{

    private String forgeVersion;

    public ForgePack(URL fileDownload) {
        super(fileDownload);
    }

    public String getForgeVersion() {
        return forgeVersion;
    }
}
