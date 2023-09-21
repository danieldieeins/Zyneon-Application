package live.nerotv.zyneon.app.application.backend.instance;

import live.nerotv.zyneon.app.application.backend.utils.file.Config;

public class FabricInstance extends VanillaInstance {

    private String fabricVersion;

    public FabricInstance(Config json) {
        super(json);
        fabricVersion = json.getString("modpack.fabric");
    }

    public String getFabricVersion() {
        return fabricVersion;
    }

    @Override
    public void unload() {
        fabricVersion = null;
        super.unload();
    }
}