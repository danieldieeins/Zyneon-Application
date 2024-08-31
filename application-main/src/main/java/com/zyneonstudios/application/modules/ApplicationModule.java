package com.zyneonstudios.application.modules;

import com.zyneonstudios.application.main.NexusApplication;

public class ApplicationModule {

    private ModuleConnector connector;

    private final String id;
    private final String name;
    private final String version;
    private final String authors;

    private final NexusApplication application;

    public ApplicationModule(NexusApplication application, String id, String name, String version, String authors) {
        NexusApplication.getLogger().dbg("[MODULES] Creating object for "+name+" ("+id+") v"+version);

        this.id = id;
        this.name = name;
        this.version = version;

        this.application = application;

        this.connector = new ModuleConnector(this);
        this.authors = authors;
    }

    public NexusApplication getApplication() {
        return application;
    }

    public ModuleConnector getConnector() {
        return connector;
    }

    @SuppressWarnings("unused")
    public void setConnector(ModuleConnector connector) {
        this.connector = connector;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getVersion() {
        return version;
    }

    public String getAuthors() {
        return authors;
    }

    public void onLoad() {
        NexusApplication.getLogger().log("[MODULES] loading "+name+" ("+id+"-v"+version+") by "+authors+"...");
    }

    public void onEnable() {
        NexusApplication.getLogger().log("[MODULES] enabling "+name+" ("+id+"-v"+version+") by "+authors+"...");
    }

    public void onDisable() {
        NexusApplication.getLogger().log("[MODULES] disabling "+name+" ("+id+"-v"+version+") by "+authors+"...");
    }
}