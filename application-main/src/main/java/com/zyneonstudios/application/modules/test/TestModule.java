package com.zyneonstudios.application.modules.test;

import com.zyneonstudios.application.main.NexusApplication;
import com.zyneonstudios.application.modules.ApplicationModule;
import com.zyneonstudios.application.modules.ModuleConnector;

public class TestModule extends ApplicationModule {

    private final NexusApplication application;
    private final ModuleConnector connector;

    public TestModule(NexusApplication application) {
        super(application, "internal-test", "Test", "2024.5", "nerotvlive");
        this.application = application;
        this.connector = new TestConnector(this);
    }

    @Override
    public NexusApplication getApplication() {
        return application;
    }

    @Override
    public ModuleConnector getConnector() {
        return connector;
    }

    @Override
    public void onLoad() {

    }

    @Override
    public void onEnable() {

    }

    @Override
    public void onDisable() {

    }
}
