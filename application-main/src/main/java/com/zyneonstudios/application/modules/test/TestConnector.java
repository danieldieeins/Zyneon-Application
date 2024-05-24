package com.zyneonstudios.application.modules.test;

import com.zyneonstudios.application.frame.web.ApplicationFrame;
import com.zyneonstudios.application.main.NexusApplication;
import com.zyneonstudios.application.modules.ApplicationModule;
import com.zyneonstudios.application.modules.ModuleConnector;

public class TestConnector extends ModuleConnector {

    private final ApplicationModule module;
    private final ApplicationFrame frame;

    public TestConnector(ApplicationModule module) {
        super(module);
        this.module = module;
        this.frame = (ApplicationFrame) module.getApplication().getFrame();
    }

    @Override
    public void resolveRequest(String request) {
        NexusApplication.getLogger().log("[REQUEST-READER] "+request);
        if(request.startsWith("init.")) {
            frame.executeJavaScript("addMenuEntry('test-page-button','bx bx-test-tube','Test','test.page');");
            request = request.replaceFirst("init.","");
            if(request.equals("test")) {
                resolveModuleRequest("init");
            }
        } else if(request.startsWith("test.")) {
            resolveModuleRequest(request.replaceFirst("test.",""));
        }
    }

    public void resolveModuleRequest(String request) {
        if(request.equals("page")) {
            frame.openCustomPage("Test","test","https://www.zyneonstudios.com");
        } else if(request.equals("init")) {
            frame.executeJavaScript("document.getElementById('test-page-button').classList.add('highlighted');");
        }
    }

    public ApplicationModule getModule() {
        return module;
    }
}
