package com.zyneonstudios.application.modules.search;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import live.nerotv.shademebaby.utils.GsonUtil;

import java.util.ArrayList;
import java.util.HashMap;

public class ModuleSearch {

    private final JsonArray array;
    private ArrayList<HashMap<String,String>> cachedResults = null;
    private String cachedSearchTerm = null;

    public ModuleSearch(String zyndexUrl) {
        array = GsonUtil.getObject(zyndexUrl).getAsJsonArray("modules");
    }

    public ArrayList<HashMap<String, String>> search(String searchTerm) {
        if(!searchTerm.replace(" ","").isEmpty()) {
            cachedSearchTerm = searchTerm;
        }

        ArrayList<HashMap<String, String>> results = new ArrayList<>();
        String[] searchTerms = searchTerm.toLowerCase().replace(" ",",").replace(",,",",").split(",");

        if(!array.isEmpty()) {
            for(JsonElement element:array) {
                JsonObject result = GsonUtil.getObject(element.getAsString()).getAsJsonObject("module");

                JsonObject info = result.getAsJsonObject("info");
                String name = info.get("name").getAsString();

                JsonObject meta = result.getAsJsonObject("meta");
                String id = meta.get("id").getAsString();

                boolean idMatching = false;
                for(String s:searchTerms) {
                    if (id.equalsIgnoreCase(s)) {
                        idMatching = true;
                        break;
                    }
                }

                if(name.toLowerCase().contains(searchTerm)||idMatching) {
                    JsonObject resources = result.getAsJsonObject("resources");
                    HashMap<String,String> module = new HashMap<>();

                    module.put("info.author",info.get("author").getAsString().replace("\"","''"));
                    module.put("info.description",info.get("description").getAsString().replace("\\n","<br>").replace("\n","<br>").replace("\"","''"));
                    module.put("info.name",info.get("name").getAsString().replace("\"","''"));
                    module.put("info.version",info.get("version").getAsString().replace("\"","''"));

                    module.put("meta.download",meta.get("download").getAsString());
                    module.put("meta.id",id);
                    module.put("meta.isHidden",meta.get("isHidden").getAsString());
                    module.put("meta.location",meta.get("location").getAsString());
                    module.put("meta.origin",meta.get("origin").getAsString());
                    module.put("meta.tags",meta.get("tags").toString());

                    if(resources.get("thumbnail")!=null) {
                        module.put("resources.thumbnail",resources.get("thumbnail").getAsString());
                    } else {
                        module.put("resources.thumbnail","");
                    }

                    if(resources.get("logo")!=null) {
                        module.put("resources.logo",resources.get("logo").getAsString());
                    } else {
                        module.put("resources.logo","");
                    }

                    if(resources.get("icon")!=null) {
                        module.put("resources.icon",resources.get("icon").getAsString());
                    } else {
                        module.put("resources.icon","");
                    }

                    if(resources.get("background")!=null) {
                        module.put("resources.background",resources.get("background").getAsString());
                    } else {
                        module.put("resources.background","");
                    }

                    results.add(module);
                }
            }
        }

        cachedResults = results;
        return cachedResults;
    }

    public ArrayList<HashMap<String, String>> getCachedResults() {
        return cachedResults;
    }

    public String getCachedSearchTerm() {
        return cachedSearchTerm;
    }
}