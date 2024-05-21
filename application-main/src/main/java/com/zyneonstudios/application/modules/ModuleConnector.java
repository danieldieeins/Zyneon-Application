package com.zyneonstudios.application.modules;

public class ModuleConnector {

    private final ApplicationModule module;

    public ModuleConnector(ApplicationModule module) {
        this.module = module;
    }

    public void resolveFrameRequest(String request) {

    }

    public void resolveBackendRequest(String request) {

    }

    public void resolveRequest(String request) {
        resolveFrameRequest(request);
        resolveBackendRequest(request);
    }
}
