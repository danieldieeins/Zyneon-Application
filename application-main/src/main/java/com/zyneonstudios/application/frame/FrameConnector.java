package com.zyneonstudios.application.frame;

import com.zyneonstudios.application.frame.web.ApplicationFrame;
import com.zyneonstudios.application.main.ApplicationConfig;
import com.zyneonstudios.application.main.NexusApplication;
import com.zyneonstudios.application.modules.ApplicationModule;
import com.zyneonstudios.application.modules.search.ModuleSearch;

import java.awt.*;
import java.util.HashMap;

public class FrameConnector {

    /*
     * Zyneon Application frame connector
     * by nerotvlive
     * Contributions are welcome. Please add your name to the "by" line if you make any modifications.
     * */

    // Instance variable to hold the ApplicationFrame object
    private final ApplicationFrame frame;
    private ModuleSearch moduleSearch = new ModuleSearch("https://raw.githubusercontent.com/zyneonstudios/nexus-nex/main/zyndex/index.json");

    // Constructor for FrameConnector class
    public FrameConnector(ApplicationFrame frame) {
        // Initializing the frame object
        this.frame = frame;
    }

    // Method to resolve requests received by the FrameConnector
    public void resolveRequest(String request) {
        //If test or debug is enabled, print out every request
        if(ApplicationConfig.test) {
            NexusApplication.getLogger().error("[CONNECTOR] (Request-Reader) resolving "+request+"...");
        } else {
            NexusApplication.getLogger().debug("[CONNECTOR] (Request-Reader) resolving "+request+"...");
        }

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
        if(request.equals("discover")) {
            frame.executeJavaScript("activateMenu('menu',true); document.getElementById('search-bar').disabled = false; document.getElementById('search-bar').placeholder = searchTerm;");
        } else {
            frame.executeJavaScript("deactivateMenu('menu',true);");
        }
        if(request.equals("settings")) {
            frame.executeJavaScript("syncVersion(\""+ApplicationConfig.getApplicationVersion().replace("\"","''")+"\");");
        }
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
            String fReq = request_[0];
            if(request_[0].equalsIgnoreCase("../assets/cronos/css/app-colors-dark.css")) {
                background = Color.black;
                foreground = Color.white;
            } else if(request_[0].equalsIgnoreCase("../assets/cronos/css/app-colors-light.css")) {
                background = Color.white;
                foreground = Color.black;
            } else if(request_[0].equalsIgnoreCase("../assets/application/css/app-colors-oled.css")) {
                background = Color.black;
                foreground = Color.white;
            } else if(request_[0].startsWith("automatic-")) {
                request = request_[0].replaceFirst("automatic-","");
                if(request.equals("dark")) {
                    background = Color.black;
                    foreground = Color.white;
                } else {
                    background = Color.white;
                    foreground = Color.black;
                }
                fReq = "automatic";
            } else {
                background = Color.decode("#0a0310");
                foreground = Color.white;
            }
            if(!ApplicationConfig.theme.equalsIgnoreCase(fReq)) {
                ApplicationConfig.theme = fReq;
                ApplicationConfig.getSettings().set("settings.theme", ApplicationConfig.theme);
            }
            String title = request_[1];
            // Set the titlebar with the specified title, background, and foreground colors
            frame.setTitlebar(title,background,foreground);
        } else if(request.equals("exit")) {
            NexusApplication.stop();
        } else if(request.equals("refresh")) {
            frame.getBrowser().loadURL(ApplicationConfig.urlBase+ApplicationConfig.language+"/"+ApplicationConfig.startPage);
        } else if(request.equals("restart")) {
            frame.getApplication().restart();
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
        } else if(request.startsWith("discover.")) {
            syncDiscover(request.replaceFirst("discover.",""));
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
            String channel = "experimental"; boolean autoUpdate = false;
            // Retrieve auto-update and update channel settings
            if(ApplicationConfig.getUpdateSettings().getBoolean("updater.settings.autoUpdate")!=null) {
                autoUpdate = ApplicationConfig.getUpdateSettings().getBool("updater.settings.autoUpdate");
            }
            if(ApplicationConfig.getUpdateSettings().getString("updater.settings.updateChannel")!=null) {
                channel = ApplicationConfig.getUpdateSettings().getString("updater.settings.updateChannel");
            }
            // Execute JavaScript to update the UI with retrieved settings
            frame.executeJavaScript("updates = "+autoUpdate+"; document.getElementById('updater-settings-enable-updates').checked = updates; document.getElementById('updater-settings-update-channel').value = \""+channel+"\"; document.getElementById('updater-settings').style.display = 'inherit'; document.getElementById('general-settings-start-page').value = '"+ApplicationConfig.startPage+"'; document.getElementById('updater-settings').style.display = 'inherit';");
        } else if(request.equals("about")) {
            frame.executeJavaScript("document.getElementById('settings-global-application-version').innerText = \""+ApplicationConfig.getApplicationVersion()+"\"");
        }
    }

    private void syncDiscover(String request) {
        if(request.startsWith("search.")) {
            if(request.replace("search.","").equals("modules")) {
                if(moduleSearch==null) {
                    moduleSearch = new ModuleSearch("https://raw.githubusercontent.com/zyneonstudios/nexus-nex/main/zyndex/index.json");
                }
                if(moduleSearch.getCachedResults()==null) {
                    moduleSearch.search("");
                }
                if(moduleSearch.getCachedSearchTerm()!=null) {
                    if(!moduleSearch.getCachedSearchTerm().isEmpty()&&!moduleSearch.getCachedSearchTerm().isBlank()) {
                        frame.executeJavaScript("document.getElementById(\"search-bar\").placeholder = \""+moduleSearch.getCachedSearchTerm()+"\";");
                    }
                }
                for(HashMap<String,String> result : moduleSearch.getCachedResults()) {
                    String tags = "Tags: "+result.get("meta.tags").replace("[\"","").replace("\"]","").replace("\"","").replace(",",", ");
                    String meta = result.get("meta.id")+" | v"+result.get("info.version")+" | Hidden: "+result.get("meta.isHidden")+"<br>"+tags;
                    String actions = "<a onclick=\\\"connector('sync.discover.details.module.nexus-minecraft-module');\\\"><i class='bx bx-spreadsheet'></i> More</a> ";
                    if(NexusApplication.getModuleLoader().getModuleIds().contains(result.get("meta.id"))) {
                        actions = "v"+NexusApplication.getModuleLoader().getModules().get(result.get("meta.id")).getVersion()+"  <a onclick=\\\"connector('sync.discover.details.module.nexus-minecraft-module');\\\"><i class='bx bx-spreadsheet'></i> More</a> <a style=\\\"background: #473e5c !important; color: white!important; cursor: not-allowed !important; box-shadow: 0 0 0.2rem var(--shadow3) !important;\\\"><i class='bx bx-check'></i> Installed</a>";
                    } else {
                        actions = actions+"<a style=\\\"background: #5632a8; color: white;\\\" onclick=\\\"connector('sync.discover.install.module.nexus-minecraft-module');\\\"><i class='bx bx-download'></i> Install</a>";
                    }
                    String command = "addResult(\""+result.get("meta.id")+"\",\""+result.get("resources.thumbnail")+"\",\""+result.get("info.name")+"\",\""+result.get("info.author")+"\",\""+result.get("info.description")+"\",\""+meta+"\",\""+actions+"\");";
                    frame.executeJavaScript(command);
                }
            }
        } else if(request.startsWith("details.")) {
            request = request.replaceFirst("details.","");
            frame.executeJavaScript("enableOverlay(\"https://www.zyneonstudios.com\");");
        }
    }
}
