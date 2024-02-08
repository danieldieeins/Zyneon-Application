package com.zyneonstudios.application.backend.integrations.modrinth;

import com.google.gson.JsonObject;
import com.zyneonstudios.Main;
import live.nerotv.shademebaby.utils.GsonUtil;

public class ModrinthResourcepacks {

    public static JsonObject search(String query, String version, int offset, int limit) {
        try {
            return GsonUtil.getObject("https://api.modrinth.com/v2/search?query="+query.toLowerCase()+"&facets=[[%22versions:"+version+"%22],[%22project_type:resourcepack%22]]&offset="+offset+"&limit="+limit);
        } catch (Exception e) {
            Main.getLogger().error("[MODRINTH] (RESOURCE PACKS) Couldn't complete search: "+e.getMessage());
            return null;
        }
    }

    @Deprecated
    public static JsonObject search(String version, int offset, int limit) {
        try {
            return GsonUtil.getObject("https://api.modrinth.com/v2/search?facets=[[%22versions:"+version+"%22],[%22project_type:resourcepack%22]]&offset="+offset+"&limit="+limit);
        } catch (Exception e) {
            Main.getLogger().error("[MODRINTH] (RESOURCE PACKS) Couldn't complete search: "+e.getMessage());
            return null;
        }
    }
}