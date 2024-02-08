package com.zyneonstudios.application.backend.integrations.zyneon;

import com.zyneonstudios.Main;

import java.util.ArrayList;
import java.util.HashMap;

public class ZyneonShaders {

    @Deprecated
    public static ArrayList<String> search(String query, String version) {
        version = version.replace(".","-");
        try {
            HashMap<String, ArrayList<String>> search = ZyneonIntegration.getFromServer("shaders");
            if(search!=null) {
                if(!search.isEmpty()) {
                    if(search.get(version)!=null) {
                        ArrayList<String> results = search.get(version);
                        ArrayList<String> results_ = new ArrayList<>();
                        for(String result:results) {
                            String result_ = ZyneonIntegration.format(result.toLowerCase());
                            String query_ = ZyneonIntegration.format(query.toLowerCase());
                            if(result_.contains(query_)) {
                                results_.add(result);
                            }
                        }
                        return results_;
                    }
                }
            }
            throw new NullPointerException("No search results");
        } catch (Exception e) {
            Main.getLogger().error("[ZYNEON] (INSTANCES) Couldn't complete search: "+e.getMessage());
            return null;
        }
    }
}
