package com.zyneonstudios.application.utils;

public interface Storage {

    default Object get(String path) {
        return null;
    }

    default boolean getBool(String path) {
        return false;
    }

    default Boolean getBoolean(String path) {
        return null;
    }

    default double getDoub(String path) {
        return -1;
    }

    default Double getDouble(String path) {
        return null;
    }

    default int getInt(String path) {
        return -1;
    }

    default Integer getInteger(String path) {
        return null;
    }

    default String getString(String path) {
        return null;
    }
}