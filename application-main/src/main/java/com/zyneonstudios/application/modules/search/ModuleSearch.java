package com.zyneonstudios.application.modules.search;

import com.zyneonstudios.nexus.index.ReadableZyndex;
import com.zyneonstudios.nexus.modules.ReadableModule;

import java.util.ArrayList;

public class ModuleSearch {

    private final ArrayList<ReadableModule> modules;
    private ArrayList<ReadableModule> cachedResults = null;
    private String cachedSearchTerm = null;
    private final boolean officialSource;

    public ModuleSearch(String zyndexUrl) {
        modules = new ReadableZyndex(zyndexUrl).getModules();
        officialSource = isOfficial(zyndexUrl);
    }

    private boolean isOfficial(String url) {
        ArrayList<String> officialUrls = new ArrayList<>();
        officialUrls.add("https://raw.githubusercontent.com/zyneonstudios/nexus-nex/main/zyndex/index.json");
        officialUrls.add("https://zyneonstudios.github.io/nexus-nex/main/zyndex/index.json");
        officialUrls.add("https://raw.githubusercontent.com/zyneonstudios/nexus-nex/main/zyndex/index");
        officialUrls.add("https://zyneonstudios.github.io/nexus-nex/main/zyndex/index");
        officialUrls.add("https://raw.githubusercontent.com/zyneonstudios/nexus-nex/main/zyndex/");
        officialUrls.add("https://zyneonstudios.github.io/nexus-nex/main/zyndex/");
        officialUrls.add("https://raw.githubusercontent.com/zyneonstudios/nexus-nex/main/zyndex");
        officialUrls.add("https://zyneonstudios.github.io/nexus-nex/main/zyndex");
        return officialUrls.contains(url.toLowerCase());
    }

    @SuppressWarnings("all")
    public ArrayList<ReadableModule> search(String searchTerm) {
        if(!searchTerm.replace(" ","").isEmpty()) {
            cachedSearchTerm = searchTerm;
        }

        ArrayList<ReadableModule> results = new ArrayList<>();
        String[] searchTerms = searchTerm.toLowerCase().replace(" ",",").replace(",,",",").split(",");

        if(!modules.isEmpty()) {
            for(ReadableModule module : modules) {
                boolean idMatching = false;
                for(String s:searchTerms) {
                    if (module.getId().equalsIgnoreCase(s)) {
                        idMatching = true;
                        break;
                    }
                }

                if(module.getName().toLowerCase().contains(searchTerm.toLowerCase())||idMatching) {
                    if(!module.isHidden()||idMatching) {
                        results.add(module);
                    }
                }
            };
        }

        cachedResults = results;
        return cachedResults;
    }

    public ArrayList<ReadableModule> getCachedResults() {
        return cachedResults;
    }

    public String getCachedSearchTerm() {
        return cachedSearchTerm;
    }
}