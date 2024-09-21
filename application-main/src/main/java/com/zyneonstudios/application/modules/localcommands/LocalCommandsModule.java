package com.zyneonstudios.application.modules.localcommands;

import com.zyneonstudios.application.main.NexusApplication;
import com.zyneonstudios.application.modules.ApplicationModule;
import com.zyneonstudios.application.modules.ModuleConnector;

public class LocalCommandsModule extends ApplicationModule {

    private final LocalCommandsConnector connector;

    public LocalCommandsModule(NexusApplication application) {
        super(application, "nexus-local-commands", "Local Commands", "2024.6", "nerotvlive");
        connector = new LocalCommandsConnector(this);
    }

    @Override
    public ModuleConnector getConnector() {
        return connector;
    }

    @Override
    public void onLoad() {
        super.onLoad();
    }

    @Override
    public void onEnable() {
        super.onEnable();
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }
}