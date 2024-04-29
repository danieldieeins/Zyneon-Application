package com.zyneonstudios.application.frame;

import com.zyneonstudios.Main;

import java.awt.*;

public class FrameConnector {

    private final ApplicationFrame frame;

    public FrameConnector(ApplicationFrame frame) {
        this.frame = frame;
    }

    public void resolveRequest(String request) {
        System.out.println(" ");
        System.out.println("[CONNECTOR] resolving "+request+"...");
        if(request.startsWith("sync.")) {
            sync(request.replace("sync.",""));
            System.out.println("[CONNECTOR] successfully resolved "+request);
        } else {
            System.err.println("[CONNECTOR] couldn't resolve "+request+".");
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
            frame.setTitle(title,background,foreground);
        } else if(request.startsWith("settings.")) {
            syncSettings(request.replaceFirst("settings.",""));
        } else if(request.startsWith("autoUpdates.")) {
            request = request.replace("autoUpdates.","");

            boolean update = request.equals("on");
            Main.getUpdaterConfig().set("updater.settings.autoUpdate",update);

            frame.executeJavaScript("document.getElementById('updater-settings-enable-updates').checked = "+update+";");
        } else if(request.startsWith("updateChannel.")) {
            request = request.replace("updateChannel.","");
            Main.getUpdaterConfig().set("updater.settings.updateChannel",request);
            System.out.println(Main.getUpdaterConfig().getJsonFile().getAbsolutePath());
            System.out.println(Main.getUpdaterConfig().getString("updater.settings.updateChannel"));
        }
    }

    private void syncSettings(String request) {
        if(request.equals("general")) {
            String channel = "shervann"; boolean autoUpdate = false;
            if(Main.getUpdaterConfig().getBoolean("updater.settings.autoUpdate")!=null) {
                autoUpdate = Main.getUpdaterConfig().getBool("updater.settings.autoUpdate");
            }
            if(Main.getUpdaterConfig().getString("updater.settings.updateChannel")!=null) {
                channel = Main.getUpdaterConfig().getString("updater.settings.updateChannel");
            }
            frame.executeJavaScript("updates = "+autoUpdate+"; document.getElementById('updater-settings-enable-updates').checked = updates; document.getElementById('updater-settings-update-channel').value = \""+channel+"\";");
        }
    }
}