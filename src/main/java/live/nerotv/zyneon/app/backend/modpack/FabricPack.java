package live.nerotv.zyneon.app.backend.modpack;

import java.net.URL;

public class FabricPack extends Modpack {

    private String fabricVersion;

    public FabricPack(URL fileDownload) {
        super(fileDownload);
    }

    public String getFabricVersion() {
        return fabricVersion;
    }
}