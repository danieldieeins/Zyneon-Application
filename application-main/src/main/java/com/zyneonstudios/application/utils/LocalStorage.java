package com.zyneonstudios.application.utils;

import java.util.HashMap;
import java.util.UUID;

public class LocalStorage implements Storage {

    private final UUID uuid;
    private final HashMap<String, Object> map = new HashMap<>();

    public LocalStorage(UUID uuid) {
        this.uuid = uuid;
    }

    public LocalStorage() {
        this.uuid = UUID.randomUUID();
    }

    @Override
    public Object get(String path) {
        return map.get(path);
    }

    @Override @Deprecated
    public boolean getBool(String path) {
        return getBoolean(path);
    }

    @Override
    public Boolean getBoolean(String path) {
        return (Boolean)map.get(path);
    }

    @Override @Deprecated
    public double getDoub(String path) {
        return getDouble(path);
    }

    @Override
    public Double getDouble(String path) {
        return (Double)map.get(path);
    }

    @Override @Deprecated
    public int getInt(String path) {
        return getInteger(path);
    }

    @Override
    public Integer getInteger(String path) {
        return (Integer)map.get(path);
    }

    @Override
    public String getString(String path) {
        return map.get(path).toString();
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getId() {
        return uuid.toString();
    }

    public void delete(String path) {
        map.remove(path);
    }

    public void clear() {
        map.clear();
    }

    public void set(String path, Object value) {
        delete(path);
        map.put(path,value);
    }

    public void setBoolean(String path, boolean value) {
        set(path,value);
    }

    public void setDouble(String path, double value) {
        set(path,value);
    }

    public void setInteger(String path, int value) {
        set(path,value);
    }

    public void setString(String path, String value) {
        set(path,value);
    }
}