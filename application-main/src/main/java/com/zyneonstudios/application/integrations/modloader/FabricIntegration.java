package com.zyneonstudios.application.integrations.modloader;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import live.nerotv.shademebaby.utils.GsonUtil;
import java.util.ArrayList;

public class FabricIntegration {
    public static ArrayList<String> getGameVersions(boolean showUnstable) {
        return getVersions(showUnstable, "https://meta.fabricmc.net/v2/versions/game");
    }

    public static ArrayList<String> getLoaderVersions(boolean showUnstable) {
        return getVersions(showUnstable, "https://meta.fabricmc.net/v2/versions/loader");
    }

    public static ArrayList<String> getLoaderVersions(boolean showUnstable, String gameVersion) {
        JsonArray versions = new Gson().fromJson(GsonUtil.getFromURL("https://meta.fabricmc.net/v2/versions/loader/"+gameVersion),JsonArray.class);
        ArrayList<String> list = new ArrayList<>();
        for(JsonElement element:versions) {
            JsonObject json = element.getAsJsonObject().get("loader").getAsJsonObject();
            if(json.get("stable").getAsBoolean()) {
                list.add(json.get("version").getAsString());
            } else {
                if(showUnstable) {
                    list.add(json.get("version").getAsString());
                }
            }
        }
        return list;
    }

    private static ArrayList<String> getVersions(boolean showUnstable, String url) {
        JsonArray versions = new Gson().fromJson(GsonUtil.getFromURL(url),JsonArray.class);
        ArrayList<String> list = new ArrayList<>();
        for(JsonElement element:versions) {
            JsonObject json = element.getAsJsonObject();
            if(json.get("stable").getAsBoolean()) {
                list.add(json.get("version").getAsString());
            } else {
                if(showUnstable) {
                    list.add(json.get("version").getAsString());
                }
            }
        }
        return list;
    }
}