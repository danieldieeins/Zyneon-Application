package com.zyneonstudios.application.modules;

@SuppressWarnings("all")
public class ModuleConnector {

    private final ApplicationModule module;

    public ModuleConnector(ApplicationModule module) {
        this.module = module;
    }

    public void resolveFrameRequest(String request) {
        resolveRequest(request);
    }

    public void resolveBackendRequest(String request) {
        resolveRequest(request);
    }

    @Deprecated
    public void resolveRequest(String request) {

    }
}
