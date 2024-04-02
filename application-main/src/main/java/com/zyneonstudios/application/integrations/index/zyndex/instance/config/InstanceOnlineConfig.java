package com.zyneonstudios.application.integrations.index.zyndex.instance.config;

import live.nerotv.shademebaby.file.OnlineConfig;
import org.jetbrains.annotations.NotNull;

@Deprecated
public class InstanceOnlineConfig extends OnlineConfig implements Comparable<InstanceOnlineConfig>{

    @Deprecated
    public InstanceOnlineConfig(String url) {
        super(url);
    }

    @Override @Deprecated
    public int compareTo(@NotNull InstanceOnlineConfig o) {
        return getString("instance.info.name").compareTo(o.getString("instance.info.name"));
    }
}
