package com.zyneonstudios.application.frame;

import com.zyneonstudios.application.frame.web.ApplicationFrame;
import com.zyneonstudios.application.main.ApplicationConfig;
import com.zyneonstudios.application.main.NexusApplication;
import com.zyneonstudios.application.modules.ApplicationModule;
import com.zyneonstudios.application.modules.search.ModuleSearch;
import live.nerotv.shademebaby.file.OnlineConfig;

import java.awt.*;
import java.net.URI;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

public class FrameConnector {

    private final ApplicationFrame frame;
    private ModuleSearch moduleSearch = new ModuleSearch("https://raw.githubusercontent.com/zyneonstudios/nexus-nex/main/zyndex/index.json");
    private final NexusApplication application;

    public FrameConnector(ApplicationFrame frame,NexusApplication application) {
        this.application = application;
        this.frame = frame;
    }

    public void resolveRequest(String request) {
        if(ApplicationConfig.test) {
            NexusApplication.getLogger().error("[CONNECTOR] (Request-Reader) resolving "+request+"...");
        } else {
            NexusApplication.getLogger().debug("[CONNECTOR] (Request-Reader) resolving "+request+"...");
        }

        if(request.startsWith("sync.")) {
            sync(request.replace("sync.", ""));
            NexusApplication.getLogger().debug("[CONNECTOR] successfully resolved " + request);
        } else if(request.startsWith("open.")) {
            open(request.replaceFirst("open.",""));
        } else if(request.startsWith("init.")) {
            init(request.replace("init.", ""));
            NexusApplication.getLogger().debug("[CONNECTOR] successfully resolved " + request);
        }
        for(ApplicationModule module:NexusApplication.getModuleLoader().getApplicationModules()) {
            module.getConnector().resolveFrameRequest(request);
        }
    }

    private void open(String request) {
        if(request.startsWith("url.")) {
            request = request.replaceFirst("url.", "");
            if(request.startsWith("decode.")) {
                request = URLDecoder.decode(request.replaceFirst("decode.", ""), StandardCharsets.UTF_8);
            }
            if(Desktop.isDesktopSupported()) {
                Desktop desktop = Desktop.getDesktop();
                try {
                    desktop.browse(new URI(request));
                } catch (Exception ignore) {}
            }
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

    private void sync(String request) {
        if(request.startsWith("title.")) {
            String[] request_ = request.replace("title.","").split("-.-",2);
            Color background;
            Color foreground;
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
            frame.setTitlebar(title,background,foreground);
        } else if(request.equals("exit")) {
            NexusApplication.stop();
        } else if(request.equals("refresh")) {
            frame.getBrowser().loadURL(ApplicationConfig.urlBase+ApplicationConfig.language+"/"+ApplicationConfig.startPage);
        } else if(request.equals("restart")) {
            application.restart();
        } else if(request.startsWith("settings.")) {
            syncSettings(request.replaceFirst("settings.",""));
        } else if(request.startsWith("autoUpdates.")) {
            request = request.replace("autoUpdates.","");
            boolean update = request.equals("on");
            ApplicationConfig.getUpdateSettings().set("updater.settings.autoUpdate",update);
            frame.executeJavaScript("document.getElementById('updater-settings-enable-updates').checked = "+update+";");
        } else if(request.startsWith("discover.")) {
            syncDiscover(request.replaceFirst("discover.",""));
        } else if(request.startsWith("updateChannel.")) {
            request = request.replace("updateChannel.","");
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

    private void syncSettings(String request) {
        if(request.equals("general")) {
            String channel = "experimental"; boolean autoUpdate = false;
            if(ApplicationConfig.getUpdateSettings().getBoolean("updater.settings.autoUpdate")!=null) {
                autoUpdate = ApplicationConfig.getUpdateSettings().getBool("updater.settings.autoUpdate");
            }
            if(ApplicationConfig.getUpdateSettings().getString("updater.settings.updateChannel")!=null) {
                channel = ApplicationConfig.getUpdateSettings().getString("updater.settings.updateChannel");
            }
            frame.executeJavaScript("updates = "+autoUpdate+"; document.getElementById('updater-settings-enable-updates').checked = updates; document.getElementById('updater-settings-update-channel').value = \""+channel+"\"; document.getElementById('updater-settings').style.display = 'inherit'; document.getElementById('general-settings-start-page').value = '"+ApplicationConfig.startPage+"'; document.getElementById('updater-settings').style.display = 'inherit';");
        } else if(request.equals("about")) {
            frame.executeJavaScript("document.getElementById('settings-global-application-version').innerText = \""+ApplicationConfig.getApplicationVersion()+"\"");
        }
    }

    private void syncDiscover(String request) {
        if(request.startsWith("search.")) {
            if(request.replace("search.","").equals("modules")) {
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
                    String location = URLEncoder.encode(result.get("meta.location"),StandardCharsets.UTF_8);
                    String actions = "<a onclick=\\\"connector('sync.discover.details.module."+location+"');\\\"><i class='bx bx-spreadsheet'></i> More</a> ";
                    if(NexusApplication.getModuleLoader().getModuleIds().contains(result.get("meta.id"))) {
                        actions = "v"+NexusApplication.getModuleLoader().getModules().get(result.get("meta.id")).getVersion()+"  <a onclick=\\\"connector('sync.discover.details.module."+location+"');\\\"><i class='bx bx-spreadsheet'></i> More</a> <a style=\\\"background: #473e5c !important; color: white!important; cursor: not-allowed !important; box-shadow: 0 0 0.2rem var(--shadow3) !important;\\\"><i class='bx bx-check'></i> Installed</a>";
                    } else {
                        actions = actions+"<a style=\\\"background: #5632a8; color: white;\\\" onclick=\\\"connector('sync.discover.install.module.nexus-minecraft-module');\\\"><i class='bx bx-download'></i> Install</a>";
                    }
                    String command = "addResult(\""+result.get("meta.id")+"\",\""+result.get("resources.thumbnail")+"\",\""+result.get("info.name")+"\",\""+result.get("info.authors")+"\",\""+formatForDetails(result.get("info.summary"))+"\",\""+meta+"\",\""+actions+"\",\""+location+"\");";
                    frame.executeJavaScript(command);
                }
            }
        } else if(request.startsWith("details.")) {
            request = request.replaceFirst("details.","");
            if(request.startsWith("module.")) {
                OnlineConfig module = new OnlineConfig(URLDecoder.decode(request.replaceFirst("module.",""),StandardCharsets.UTF_8));
                initDetails(module.getString("module.info.name"),module.getString("module.meta.id"),"Application module",module.getString("module.info.version"),module.getString("module.info.summary"),module.getString("module.info.authors"),module.getBool("module.meta.isHidden"),module.get("module.meta.tags").toString(),module.getString("module.meta.description"),module.get("module.meta.changelogs").toString(),module.get("module.meta.versions").toString(),module.getString("module.style.info"),module.getString("module.style.card"),module.getString("module.resources.background"),module.getString("module.resources.icon"),module.getString("module.resources.logo"));
            }
        }
    }

    private void initDetails(String name, String id, String type, String version, String summary, String authors, boolean isHidden, String tags, String description, String changelog, String versions, String customInfoHTML, String customInfoCardHTML, String background, String icon, String logo) {
        String url = ApplicationConfig.urlBase+ApplicationConfig.language+"/sub-details.html";
        if(name!=null) {
            url = url+"?name="+formatForDetails(name);
        }
        if(id!=null) {
            url = url+"&id="+formatForDetails(id);
        }
        if(type!=null) {
            url = url+"&type="+formatForDetails(type);
        }
        if(version!=null) {
            url = url+"&version="+formatForDetails(version);
        }
        if(summary!=null) {
            url = url+"&summary="+formatForDetails(summary);
        }
        if(authors!=null) {
            url = url+"&authors="+formatForDetails(authors);
        }
        url = url+"&hidden="+isHidden;
        if(tags!=null) {
            url = url+"&tags="+formatForDetails(tags.replace("[","").replace("]",""));
        }
        if(description!=null) {
            url = url+"&description="+formatForDetails(description);
        } else {
            if(summary!=null) {
                url = url+"&description="+formatForDetails(summary);
            }
        }
        if(changelog!=null) {
            url = url+"&changelog="+formatForDetails(changelog.replace("[","").replace("]","").replace(", ",","));
        }
        if(versions!=null) {
            url = url+"&versions="+formatForDetails(versions.replace("[","").replace("]","").replace(", ",","));
        }
        if(customInfoHTML!=null) {
            url = url+"&c="+formatForDetails(customInfoHTML);
        }
        if(customInfoCardHTML!=null) {
            url = url+"&cc="+formatForDetails(customInfoCardHTML);
        }
        if(background!=null) {
            String b = "&background="+formatForDetails(background);
            url = url+b;
            System.out.println(b);
        }
        if(icon!=null) {
            url = url+"&icon="+formatForDetails(icon);
        }
        if(logo!=null) {
            url = url+"&logo="+formatForDetails(logo);
        }
        url = url.replace("\\","/");
        frame.executeJavaScript("enableOverlay(\""+url+"\");");
    }

    private String formatForDetails(String input) {
        if(input!=null) {
            if(!input.isBlank()&&!input.isEmpty()) {
                input = input.replace("\"","''");
                input = input.replace("\n","<br>").replace("\\n","<br>").replace("+","%plus%");
                return URLEncoder.encode(input, StandardCharsets.UTF_8).replace("+", "%20").replace("%plus%", "+");
            }
            return "";
        }
        return null;
    }
}
