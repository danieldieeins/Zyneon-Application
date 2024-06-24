package com.zyneonstudios.application.modules.localcommands;

import com.zyneonstudios.application.frame.web.ApplicationFrame;
import com.zyneonstudios.application.main.ApplicationConfig;
import com.zyneonstudios.application.modules.ModuleConnector;

public class LocalCommandsConnector extends ModuleConnector {

    private final LocalCommandsModule module;
    private final ApplicationFrame frame;

    public LocalCommandsConnector(LocalCommandsModule module) {
        super(module);
        this.module = module;
        this.frame = (ApplicationFrame)module.getApplication().getFrame();
    }

    @Override
    public void resolveFrameRequest(String request) {
        if(request.equals("init.library")) {
            frame.executeJavaScript("addModuleToList(\""+module.getName()+"\",\""+module.getId()+"\",\""+"\")");
        } else if(request.equals("sync.library.module.nexus-local-commands")) {
            frame.executeJavaScript("document.getElementById(\"select-game-module\").value = \""+module.getId()+"\"; addAction(\"Add command\",\"bx bx-plus\",\"connector('lc.library.action.add.command');\",'lc-action-add-command'); addAction(\"Refresh commands\",\"bx bx-refresh\",\"connector('lc.library.action.refresh.commands');\",'lc-action-refresh-commands'); addGroup(\"Commands\",\"lc-group-commands\");");
        } else if(request.equals("lc.library.action.add.command")) {
            frame.executeJavaScript("enableOverlay(\""+ApplicationConfig.urlBase+ApplicationConfig.language+"/lc-create-command.html\");");
        }
    }
}
