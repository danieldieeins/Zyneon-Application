package com.zyneonstudios.application.backend.integrations.modrinth;

import com.google.gson.JsonObject;
import com.zyneonstudios.Main;
import fr.flowarg.openlauncherlib.NoFramework;
import live.nerotv.shademebaby.utils.GsonUtil;

public class ModrinthMods {

    public static JsonObject search(String query, NoFramework.ModLoader modLoader, String version, int offset, int limit) {
        try {
            String search = "https://api.modrinth.com/v2/search?query="+query.toLowerCase()+"&facets=[[%22categories:"+modLoader.toString().toLowerCase()+"%22],[%22versions:"+version+"%22],[%22project_type:mod%22]]&offset="+offset+"&limit="+limit;
            return GsonUtil.getObject(search);
        } catch (Exception e) {
            Main.getLogger().error("[MODRINTH] (MODS) Couldn't complete search: "+e.getMessage());
            return null;
        }
    }

    @Deprecated
    public static JsonObject search(NoFramework.ModLoader modLoader, String version, int offset, int limit) {
        try {
            return GsonUtil.getObject("https://api.modrinth.com/v2/search?facets=[[%22categories:"+modLoader.toString().toLowerCase()+"%22],[%22versions:"+version+"%22],[%22project_type:mod%22]]&offset="+offset+"&limit="+limit);
        } catch (Exception e) {
            Main.getLogger().error("[MODRINTH] (MODS) Couldn't complete search: "+e.getMessage());
            return null;
        }
    }
}