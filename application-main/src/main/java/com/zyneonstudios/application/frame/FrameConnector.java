package com.zyneonstudios.application.frame;

import com.zyneonstudios.application.frame.web.ApplicationFrame;
import com.zyneonstudios.application.main.ApplicationConfig;
import com.zyneonstudios.application.main.NexusApplication;

import java.awt.*;

public class FrameConnector {

    private final ApplicationFrame frame;

    public FrameConnector(ApplicationFrame frame) {
        this.frame = frame;
    }

    public void resolveRequest(String request) {
        NexusApplication.getLogger().debug("[CONNECTOR] resolving "+request+"...");
        if(request.startsWith("sync.")) {
            sync(request.replace("sync.",""));
            NexusApplication.getLogger().debug("[CONNECTOR] successfully resolved "+request);
        } else {
            NexusApplication.getLogger().error("[CONNECTOR] couldn't resolve "+request+".");
        }

    }

    private void sync(String request) {
        frame.executeJavaScript("syncDesktop();");
        if(request.startsWith("title.")) {
            String[] request_ = request.replace("title.","").split("-.-",2);
            Color background;
            Color foreground;
            if(request_[0].equalsIgnoreCase("assets/cronos/css/app-colors-dark.css")) {
                background = Color.black;
                foreground = Color.white;
            } else if(request_[0].equalsIgnoreCase("assets/cronos/css/app-colors-light.css")) {
                background = Color.white;
                foreground = Color.black;
            } else {
                background = Color.decode("#0a0310");
                foreground = Color.white;
            }
            String title = request_[1];
            frame.setTitlebar(title,background,foreground);
        } else if(request.startsWith("settings.")) {
            syncSettings(request.replaceFirst("settings.",""));
        } else if(request.startsWith("autoUpdates.")) {
            request = request.replace("autoUpdates.","");

            boolean update = request.equals("on");
            ApplicationConfig.getUpdateSettings().set("updater.settings.autoUpdate",update);

            frame.executeJavaScript("document.getElementById('updater-settings-enable-updates').checked = "+update+";");
        } else if(request.startsWith("updateChannel.")) {
            request = request.replace("updateChannel.","");
            ApplicationConfig.getUpdateSettings().set("updater.settings.updateChannel",request);
        }
    }

    private void syncSettings(String request) {
        if(request.equals("general")) {
            String channel = "shervann"; boolean autoUpdate = false;
            if(ApplicationConfig.getUpdateSettings().getBoolean("updater.settings.autoUpdate")!=null) {
                autoUpdate = ApplicationConfig.getUpdateSettings().getBool("updater.settings.autoUpdate");
            }
            if(ApplicationConfig.getUpdateSettings().getString("updater.settings.updateChannel")!=null) {
                channel = ApplicationConfig.getUpdateSettings().getString("updater.settings.updateChannel");
            }
            frame.executeJavaScript("updates = "+autoUpdate+"; document.getElementById('updater-settings-enable-updates').checked = updates; document.getElementById('updater-settings-update-channel').value = \""+channel+"\";");
        }
    }
}