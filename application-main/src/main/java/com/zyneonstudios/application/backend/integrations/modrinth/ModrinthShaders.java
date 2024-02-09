package com.zyneonstudios.application.backend.integrations.modrinth;

import com.google.gson.JsonObject;
import com.zyneonstudios.Main;
import live.nerotv.shademebaby.utils.GsonUtil;

public class ModrinthShaders {

    public static JsonObject search(String query, String version, int offset, int limit) {
        if(version.equalsIgnoreCase("all")) {
            return search(query,offset,limit);
        }
        try {
            String search = "https://api.modrinth.com/v2/search?query="+query.toLowerCase()+"&facets=[[%22versions:"+version+"%22],[%22project_type:shader%22]]&offset="+offset+"&limit="+limit;
            return GsonUtil.getObject(search);
        } catch (Exception e) {
            Main.getLogger().error("[MODRINTH] (SHADERS) Couldn't complete search: "+e.getMessage());
            return null;
        }
    }

    public static JsonObject search(String query, int offset, int limit) {
        try {
            String search = "https://api.modrinth.com/v2/search?query="+query.toLowerCase()+"&facets=[[%22project_type:shader%22]]&offset="+offset+"&limit="+limit;
            return GsonUtil.getObject(search);
        } catch (Exception e) {
            Main.getLogger().error("[MODRINTH] (SHADERS) Couldn't complete search: "+e.getMessage());
            return null;
        }
    }
}