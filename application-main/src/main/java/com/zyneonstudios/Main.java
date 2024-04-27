package com.zyneonstudios;

import com.google.gson.JsonArray;
import com.zyneonstudios.application.NexusApplication;
import live.nerotv.shademebaby.file.Config;

public class Main {

    private static String applicationPath = null;
    private static Config config;
    private static NexusApplication application;

    private static String[] args;

    public static void main(String[] args) {
        Main.args = args;
        for(String arg:args) {
            if(arg.startsWith("--path:")) {
                applicationPath = arg.replace("--path:", "");
            }
        }
        config = new Config(applicationPath+"config/settings.json");
        final Config zyndex = new Config(applicationPath+"config/index.json");
        zyndex.set("name","NEXUS Application");
        zyndex.set("url","file://"+applicationPath+"config/index.json");
        zyndex.set("owner","Zyneon Studios, NEXUS Team");
        zyndex.checkEntry("instances",new JsonArray());
        getApplication();
    }

    public static NexusApplication getApplication() {
        if(application==null) {
            application = new NexusApplication(args);
        }
        return application;
    }

    public static Config getConfig() {
        return config;
    }

    public static String getApplicationPath() {
        if(applicationPath==null) {

        }
        return applicationPath;
    }
}