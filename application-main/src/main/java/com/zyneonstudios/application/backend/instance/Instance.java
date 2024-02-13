package com.zyneonstudios.application.backend.instance;

import live.nerotv.shademebaby.file.Config;

@SuppressWarnings("all")
public interface Instance {

    default boolean checkVersion() {
        return true;
    }

    default boolean update() {
        return true;
    }

    default void sync() {}

    default Config getJSON() {
        return null;
    }

    default String getPath() {
        return null;
    }

    default String getID() {
        return null;
    }

    default String getName() {
        return null;
    }

    default String getVersion() {
        return null;
    }

    default String getMinecraftVersion() {
        return null;
    }
}