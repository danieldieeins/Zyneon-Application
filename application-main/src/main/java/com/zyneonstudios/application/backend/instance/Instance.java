package com.zyneonstudios.application.backend.instance;

import live.nerotv.shademebaby.file.Config;

@Deprecated
public interface Instance {

    @Deprecated
    default boolean checkVersion() {
        return true;
    }

    @Deprecated
    default boolean update() {
        return true;
    }

    @Deprecated
    default void sync() {}

    @Deprecated
    default Config getJSON() {
        return null;
    }

    @Deprecated
    default Config getSettings() {
        return null;
    }

    @Deprecated
    default String getPath() {
        return null;
    }

    @Deprecated
    default String getID() {
        return null;
    }

    @Deprecated
    default String getName() {
        return null;
    }

    @Deprecated
    default String getVersion() {
        return null;
    }

    @Deprecated
    default String getMinecraftVersion() {
        return null;
    }
}