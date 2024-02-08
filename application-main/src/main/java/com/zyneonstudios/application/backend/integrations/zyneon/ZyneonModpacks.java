package com.zyneonstudios.application.backend.integrations.zyneon;

import com.zyneonstudios.Main;
import com.zyneonstudios.application.backend.instance.InstanceConfig;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class ZyneonModpacks {

    public static ArrayList<InstanceConfig> search(String query, String version) {
        if(version.equalsIgnoreCase("all")) {
            return search(query);
        }
        version = version.replace(".","-");
        try {
            HashMap<String, ArrayList<String>> search = ZyneonIntegration.getFromServer("instances");
            if(search!=null) {
                if(!search.isEmpty()) {
                    if(search.get(version)!=null) {
                        ArrayList<String> results = search.get(version);
                        ArrayList<InstanceConfig> instances = new ArrayList<>();
                        for(String result:results) {
                            String result_ = ZyneonIntegration.format(result.toLowerCase());
                            String query_ = ZyneonIntegration.format(query.toLowerCase());
                            if(result_.contains(query_)) {
                                InstanceConfig instance = new InstanceConfig("https://raw.githubusercontent.com/danieldieeins/ZyneonApplicationContent/main/m/"+result+".json");
                                if(instance.getString("modpack.hidden")!=null) {
                                    if(instance.getString("modpack.hidden").equalsIgnoreCase("false")) {
                                        instances.add(instance);
                                    } else {
                                        if(result_.equalsIgnoreCase(query_)) {
                                            instances.add(instance);
                                        }
                                    }
                                }
                            }
                        }
                        return instances;
                    }
                }
            }
            throw new NullPointerException("No search results");
        } catch (Exception e) {
            Main.getLogger().error("[ZYNEON] (INSTANCES) Couldn't complete search: "+e.getMessage());
            return null;
        }
    }

    public static ArrayList<InstanceConfig> search(String query) {
        HashMap<String, ArrayList<String>> search = ZyneonIntegration.getFromServer("instances");
        ArrayList<InstanceConfig> instances = new ArrayList<>();
        for(String key:search.keySet()) {
            ArrayList<InstanceConfig> results = search(query,key);
            instances.addAll(results);
            Collections.sort(instances);
        }
        return instances;
    }
}