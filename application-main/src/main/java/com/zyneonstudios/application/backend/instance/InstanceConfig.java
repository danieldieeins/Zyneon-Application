package com.zyneonstudios.application.backend.instance;

import live.nerotv.shademebaby.file.OnlineConfig;
import org.jetbrains.annotations.NotNull;

public class InstanceConfig extends OnlineConfig implements Comparable<InstanceConfig>{

    @Deprecated
    public InstanceConfig(String url) {
        super(url);
    }

    @Override @Deprecated
    public int compareTo(@NotNull InstanceConfig o) {
        return getString("modpack.name").compareTo(o.getString("modpack.name"));
    }
}
