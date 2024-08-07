package com.zyneonstudios.application.utils;

import live.nerotv.shademebaby.file.Config;

public class FileStorage implements Storage {

    private final Config config;

    public FileStorage(String path) {
        this.config = new Config(path);
    }

    @Override
    public Object get(String path) {
        return config.get(path);
    }

    @Override
    public boolean getBool(String path) {
        return config.getBool(path);
    }

    @Override
    public Boolean getBoolean(String path) {
        return config.getBoolean(path);
    }

    @Override
    public double getDoub(String path) {
        return config.getDoub(path);
    }

    @Override
    public Double getDouble(String path) {
        return config.getDouble(path);
    }

    @Override
    public int getInt(String path) {
        return config.getInt(path);
    }

    @Override
    public Integer getInteger(String path) {
        return config.getInteger(path);
    }

    @Override
    public String getString(String path) {
        return config.getString(path);
    }
}