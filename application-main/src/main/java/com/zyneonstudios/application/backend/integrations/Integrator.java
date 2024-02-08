package com.zyneonstudios.application.backend.integrations;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.zyneonstudios.application.Application;
import com.zyneonstudios.application.backend.instance.InstanceConfig;

import java.util.ArrayList;

public class Integrator {

    public static void modrinthToConnector(JsonObject search) {
        JsonArray results = search.getAsJsonArray("hits");
        for (int i = 0; i < results.size(); i++) {
            JsonObject item = results.get(i).getAsJsonObject();
            String item_id = item.get("project_id").getAsString();
            String item_slug = item.get("slug").getAsString();
            String item_author = item.get("author").getAsString();
            String item_title = item.get("title").getAsString();
            String item_description = item.get("description").getAsString();
            String item_png = item.get("icon_url").getAsString();
            addItemCard(item_png,item_title,item_author,item_description,item_id,item_slug);
        }
    }

    public static void zyneonToConnector(ArrayList<InstanceConfig> results) {
        if(results!=null) {
            if(!results.isEmpty()) {
                for (InstanceConfig instance : results) {
                    String id = instance.getString("modpack.id");
                    String icon = "https://danieldieeins.github.io/Zyneon-Application/content/assets/zyneon/images/instances/" + id + ".png";
                    if (instance.getString("modpack.icon") != null) {
                        icon = instance.getString("modpack.icon");
                    }
                    addItemCard(icon, instance.getString("modpack.name"), instance.getString("modpack.author"), instance.getString("modpack.description"), id, id);
                }
            }
        }
    }

    public static void addItemCard(String png, String title, String author, String description, String id, String slug) {
        String command = "addItem('"+png.replace("'","")+"','"+title.replace("'","")+"','"+author.replace("'","")+"','"+description.replace("'","")+"','"+id.replace("'","")+"','"+slug.replace("'","")+"');";
        Application.getFrame().executeJavaScript(command);
    }
}