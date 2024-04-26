package com.zyneonstudios.application.integrations.modrinth;

import com.google.gson.JsonObject;
import com.zyneonstudios.Main;
import live.nerotv.shademebaby.utils.GsonUtil;

public class ModrinthModpacks {

    public static JsonObject search(String query, String version, int offset, int limit) {
        if(version.equalsIgnoreCase("all")) {
            return search(query,offset,limit);
        }
        try {
            String search = "https://api.modrinth.com/v2/search?query="+query.toLowerCase()+"&facets=[[%22versions:"+version+"%22],[%22project_type:modpack%22]]&offset="+offset+"&limit="+limit;
            Main.getLogger().debug(search);
            return GsonUtil.getObject(search);
        } catch (Exception e) {
            Main.getLogger().error("[MODRINTH] (MODPACKS) Couldn't complete search: "+e.getMessage());
            return null;
        }
    }

    public static JsonObject search(String query, int offset, int limit) {
        try {
            String search = "https://api.modrinth.com/v2/search?query="+query.toLowerCase()+"&facets=[[%22project_type:modpack%22]]&offset="+offset+"&limit="+limit;
            Main.getLogger().debug(search);
            return GsonUtil.getObject(search);
        } catch (Exception e) {
            Main.getLogger().error("[MODRINTH] (MODPACKS) Couldn't complete search: "+e.getMessage());
            return null;
        }
    }
}