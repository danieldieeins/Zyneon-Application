package com.zyneonstudios.application.instance;

import fr.flowarg.flowupdater.versions.ForgeVersionType;
import live.nerotv.shademebaby.file.Config;

@Deprecated
public class ForgeInstance extends VanillaInstance {

    private String forgeVersion;
    private ForgeVersionType forgeType;

    @Deprecated
    public ForgeInstance(Config json) {
        super(json);
        forgeVersion = json.getString("modpack.forge.version");
        forgeType = ForgeVersionType.valueOf(json.getString("modpack.forge.type"));
    }

    @Deprecated
    public String getForgeVersion() {
        return forgeVersion;
    }

    @Deprecated
    public ForgeVersionType getForgeType() {
        return forgeType;
    }

    @Override @Deprecated
    public void unload() {
        forgeType = null;
        forgeVersion = null;
        super.unload();
    }
}