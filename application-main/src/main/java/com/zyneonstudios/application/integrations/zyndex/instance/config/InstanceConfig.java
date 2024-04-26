package com.zyneonstudios.application.integrations.zyndex.instance.config;

import live.nerotv.shademebaby.file.Config;
import org.jetbrains.annotations.NotNull;

import java.io.File;

@Deprecated
public class InstanceConfig extends Config implements Comparable<InstanceConfig>{

    @Deprecated
    public InstanceConfig(File file) {
        super(file);
    }

    @Override @Deprecated
    public int compareTo(@NotNull InstanceConfig o) {
        return getString("instance.info.name").compareTo(o.getString("instance.info.name"));
    }
}
