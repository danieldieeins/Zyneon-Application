package com.zyneonstudios.application.backend.integrations.modloader;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import live.nerotv.shademebaby.utils.GsonUtil;
import java.util.ArrayList;

public class QuiltIntegration {

    public static ArrayList<String> getGameVersions(boolean showUnstable) {
        JsonArray versions = new Gson().fromJson(GsonUtil.getFromURL("https://meta.quiltmc.org/v3/versions/game"),JsonArray.class);
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

    public static ArrayList<String> getLoaderVersions() {
        JsonArray versions = new Gson().fromJson(GsonUtil.getFromURL("https://meta.quiltmc.org/v3/versions/loader"),JsonArray.class);
        ArrayList<String> list = new ArrayList<>();
        for(JsonElement element:versions) {
            JsonObject json = element.getAsJsonObject();
            list.add(json.get("version").getAsString());
        }
        return list;
    }

    public static ArrayList<String> getLoaderVersions(String gameVersion) {
        JsonArray versions = new Gson().fromJson(GsonUtil.getFromURL("https://meta.quiltmc.org/v3/versions/loader/"+gameVersion),JsonArray.class);
        ArrayList<String> list = new ArrayList<>();
        for(JsonElement element:versions) {
            JsonObject json = element.getAsJsonObject().get("loader").getAsJsonObject();
            list.add(json.get("version").getAsString());
        }
        return list;
    }
}