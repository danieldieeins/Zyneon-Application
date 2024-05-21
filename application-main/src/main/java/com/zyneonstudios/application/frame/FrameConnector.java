package com.zyneonstudios.application.frame;

import com.zyneonstudios.application.frame.web.ApplicationFrame;
import com.zyneonstudios.application.main.ApplicationConfig;
import com.zyneonstudios.application.main.NexusApplication;
import com.zyneonstudios.application.modules.ApplicationModule;

import java.awt.*;

public class FrameConnector {

    /*
     * Zyneon Application frame connector
     * by nerotvlive
     * Contributions are welcome. Please add your name to the "by" line if you make any modifications.
     * */

    // Instance variable to hold the ApplicationFrame object
    private final ApplicationFrame frame;

    // Constructor for FrameConnector class
    public FrameConnector(ApplicationFrame frame) {
        // Initializing the frame object
        this.frame = frame;
    }

    // Method to resolve requests received by the FrameConnector
    public void resolveRequest(String request) {
        // Logging the resolution process
        NexusApplication.getLogger().debug("[CONNECTOR] resolving "+request+"...");
        // Checking the type of request
        if(request.startsWith("sync.")) {
            // If the request starts with "sync.", call the sync method
            sync(request.replace("sync.", ""));
            // Log successful resolution
            NexusApplication.getLogger().debug("[CONNECTOR] successfully resolved " + request);
        } else if(request.startsWith("init.")) {
            // If the request starts with "init.", call the init method
            init(request.replace("init.", ""));
            // Log successful resolution
            NexusApplication.getLogger().debug("[CONNECTOR] successfully resolved " + request);
        }
        for(ApplicationModule module:frame.getApplication().getModuleLoader().getApplicationModules()) {
            module.getConnector().resolveFrameRequest(request);
        }
    }

    private void init(String request) {
        frame.executeJavaScript("syncDesktop();");
    }

    // Method to synchronize settings and updates
    private void sync(String request) {
        // Execute JavaScript function to sync with desktop
        // Check the type of synchronization request
        if(request.startsWith("title.")) {
            // If the request starts with "title.", update the titlebar
            String[] request_ = request.replace("title.","").split("-.-",2);
            Color background;
            Color foreground;
            // Set title bar background and foreground colors based on the request
            if(request_[0].equalsIgnoreCase("../assets/cronos/css/app-colors-dark.css")) {
                background = Color.black;
                foreground = Color.white;
            } else if(request_[0].equalsIgnoreCase("../assets/cronos/css/app-colors-light.css")) {
                background = Color.white;
                foreground = Color.black;
            } else {
                background = Color.decode("#0a0310");
                foreground = Color.white;
            }
            String title = request_[1];
            // Set the titlebar with the specified title, background, and foreground colors
            frame.setTitlebar(title,background,foreground);
        } else if(request.startsWith("settings.")) {
            // If the request starts with "settings.", synchronize settings
            syncSettings(request.replaceFirst("settings.",""));
        } else if(request.startsWith("autoUpdates.")) {
            // If the request starts with "autoUpdates.", synchronize auto-update settings
            request = request.replace("autoUpdates.","");

            boolean update = request.equals("on");
            // Update the auto-update setting
            ApplicationConfig.getUpdateSettings().set("updater.settings.autoUpdate",update);

            // Execute JavaScript to update the UI accordingly
            frame.executeJavaScript("document.getElementById('updater-settings-enable-updates').checked = "+update+";");
        } else if(request.startsWith("updateChannel.")) {
            // If the request starts with "updateChannel.", synchronize update channel settings
            request = request.replace("updateChannel.","");
            // Update the update channel setting
            ApplicationConfig.getUpdateSettings().set("updater.settings.updateChannel",request);
        } else if(request.startsWith("startPage.")) {
            request = request.replaceFirst("startPage.","");
            ApplicationConfig.startPage = request;
            ApplicationConfig.getSettings().set("settings.startPage",request);
        } else if(request.startsWith("language.")) {
            request = request.replaceFirst("language.","");
            ApplicationConfig.language = request;
            ApplicationConfig.getSettings().set("settings.language",request);
        }
    }

    // Method to synchronize general settings
    private void syncSettings(String request) {
        if(request.equals("general")) {
            String channel = "shervann"; boolean autoUpdate = false;
            // Retrieve auto-update and update channel settings
            if(ApplicationConfig.getUpdateSettings().getBoolean("updater.settings.autoUpdate")!=null) {
                autoUpdate = ApplicationConfig.getUpdateSettings().getBool("updater.settings.autoUpdate");
            }
            if(ApplicationConfig.getUpdateSettings().getString("updater.settings.updateChannel")!=null) {
                channel = ApplicationConfig.getUpdateSettings().getString("updater.settings.updateChannel");
            }
            // Execute JavaScript to update the UI with retrieved settings
            frame.executeJavaScript("updates = "+autoUpdate+"; document.getElementById('updater-settings-enable-updates').checked = updates; document.getElementById('updater-settings-update-channel').value = \""+channel+"\"; document.getElementById('updater-settings').style.display = 'inherit'; document.getElementById('general-settings-start-page').value = '"+ApplicationConfig.startPage+"';");
        }
    }
}
