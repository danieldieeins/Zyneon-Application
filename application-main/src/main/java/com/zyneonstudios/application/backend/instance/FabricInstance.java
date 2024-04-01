package com.zyneonstudios.application.backend.instance;

import live.nerotv.shademebaby.file.Config;

@Deprecated
public class FabricInstance extends VanillaInstance {

    private String fabricVersion;

    @Deprecated
    public FabricInstance(Config json) {
        super(json);
        fabricVersion = json.getString("modpack.fabric");
    }

    @Deprecated
    public String getFabricVersion() {
        return fabricVersion;
    }

    @Override @Deprecated
    public void unload() {
        fabricVersion = null;
        super.unload();
    }
}