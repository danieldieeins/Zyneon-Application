package com.zyneonstudios.application.frame;

import com.zyneonstudios.application.frame.web.ApplicationFrame;
import com.zyneonstudios.application.main.ApplicationStorage;
import com.zyneonstudios.application.main.NexusApplication;
import com.zyneonstudios.application.modules.ApplicationModule;
import com.zyneonstudios.application.modules.search.ModuleSearch;
import com.zyneonstudios.nexus.modules.ReadableModule;

import java.awt.*;
import java.net.URI;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class FrameConnector {

    private final ApplicationFrame frame;
    private final ModuleSearch moduleSearch = new ModuleSearch("https://zyneonstudios.github.io/nexus-nex/zyndex/index.json");
    private final NexusApplication application;

    public FrameConnector(ApplicationFrame frame,NexusApplication application) {
        this.application = application;
        this.frame = frame;
    }

    public void resolveRequest(String request) {
        if(ApplicationStorage.test) {
            NexusApplication.getLogger().error("[CONNECTOR] (Request-Reader) resolving "+request+"...");
        } else {
            NexusApplication.getLogger().debug("[CONNECTOR] (Request-Reader) resolving "+request+"...");
        }

        if(request.startsWith("sync.")) {
            sync(request.replace("sync.", ""));
        } else if(request.startsWith("open.")) {
            open(request.replaceFirst("open.",""));
        } else if(request.startsWith("init.")) {
            init(request.replace("init.", ""));
        } else if(request.startsWith("load.")) {
            load(request.replace("load.", ""));
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

    private void load(String request) {
        switch (request) {
            case "drive" ->
                    frame.getBrowser().loadURL("https://drive.zyneonstudios.com/app/?theme=" + ApplicationStorage.theme + "&language=" + ApplicationStorage.language);
            case "discover" ->
                    frame.getBrowser().loadURL(ApplicationStorage.urlBase + ApplicationStorage.language + "/discover.html");
            case "downloads" ->
                    frame.getBrowser().loadURL(ApplicationStorage.urlBase + ApplicationStorage.language + "/downloads.html");
            case "library" ->
                    frame.getBrowser().loadURL(ApplicationStorage.urlBase + ApplicationStorage.language + "/library.html");
            case "settings" ->
                    frame.getBrowser().loadURL(ApplicationStorage.urlBase + ApplicationStorage.language + "/settings.html");
        }
    }

    private void init(String request) {
        frame.executeJavaScript("syncDesktop();");
        if(request.equals("discover")) {
            frame.executeJavaScript("setMenuPanel('','"+ ApplicationStorage.getApplicationVersion()+"','"+ ApplicationStorage.getApplicationName()+"',true);");
            if(frame.getBrowser().getURL().contains("&l=search")||frame.getBrowser().getURL().contains("?l=search")) {
                frame.executeJavaScript("deactivateMenu('menu',true);");
            } else {
                frame.executeJavaScript("activateMenu('menu',true); document.getElementById('search-bar').disabled = false; document.getElementById('search-bar').placeholder = searchTerm;");
                frame.executeJavaScript("document.querySelector('.fes').innerHTML = \"<i class='bx bxs-bug-alt'></i> Force disable search\"; document.querySelector('.fes').onclick = function () { document.getElementById('search-bar').disabled = true; document.getElementById('search-bar').placeholder = 'Search disabled'; }");
            }
        } else {
            frame.executeJavaScript("deactivateMenu('menu',true);");
        }
        if(request.equals("settings")) {
            frame.executeJavaScript("syncVersion(\""+ ApplicationStorage.getApplicationVersion().replace("\"","''")+"\");");
        } else if(request.equals("downloads")) {
            
        }
    }

    private void sync(String request) {
        if(request.startsWith("title.")) {
            if(ApplicationStorage.hasDriveAccess()) {
                frame.executeJavaScript("document.getElementById('drive-button').style.display = 'flex';");
            }
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
            if(!ApplicationStorage.theme.equalsIgnoreCase(fReq)) {
                ApplicationStorage.theme = fReq;
                ApplicationStorage.getSettings().set("settings.theme", ApplicationStorage.theme);
            }
            String title = request_[1];
            frame.setTitlebar(title,background,foreground);
        } else if(request.startsWith("firstrun.")) {
            request = request.replaceFirst("firstrun.","");
            if(request.equals("theme")) {
                frame.executeJavaScript("initThemeWizard();");
                if(ApplicationStorage.getOS().startsWith("Linux")) {
                    boolean customFrame = true;
                    if(ApplicationStorage.getSettings().get("settings.linux.customFrame")!=null) {
                        try {
                            customFrame = ApplicationStorage.getSettings().getBool("settings.linux.customFrame");
                        } catch (Exception ignore) {}
                    }
                    frame.executeJavaScript("initLinuxWizard('"+customFrame+"');");
                }
            } else if(request.startsWith("linuxFrame.")) {
                request = request.replace("linuxFrame.","");
                boolean frame = request.equals("on");
                ApplicationStorage.getSettings().set("settings.linux.customFrame",frame);
                ApplicationStorage.getSettings().set("cache.restartPage","firstrun.html?theme.colors="+ ApplicationStorage.theme+"#linux");
                application.restart(true);
            } else if(request.equals("finished")) {
                ApplicationStorage.getSettings().set("settings.setupFinished",true);
            }
        } else if(request.equals("exit")) {
            NexusApplication.stop();
        } else if(request.equals("refresh")) {
            frame.getBrowser().loadURL(ApplicationStorage.urlBase+ ApplicationStorage.language+"/"+ ApplicationStorage.startPage);
        } else if(request.equals("restart")) {
            application.restart(false);
        } else if(request.startsWith("settings.")) {
            syncSettings(request.replaceFirst("settings.",""));
        } else if(request.startsWith("autoUpdates.")) {
            request = request.replace("autoUpdates.","");
            boolean update = request.equals("on");
            ApplicationStorage.getUpdateSettings().set("updater.settings.autoUpdate",update);
            frame.executeJavaScript("document.getElementById('updater-settings-enable-updates').checked = "+update+";");
        } else if(request.startsWith("linuxFrame.")) {
            request = request.replace("linuxFrame.","");
            boolean frame = request.equals("on");
            ApplicationStorage.getSettings().set("settings.linux.customFrame",frame);
            ApplicationStorage.getSettings().set("cache.restartPage","settings.html");
            application.restart(true);
        } else if(request.startsWith("discover.")) {
            syncDiscover(request.replaceFirst("discover.",""));
        } else if(request.startsWith("updateChannel.")) {
            request = request.replace("updateChannel.","");
            ApplicationStorage.getUpdateSettings().set("updater.settings.updateChannel",request);
        } else if(request.startsWith("startPage.")) {
            request = request.replaceFirst("startPage.","");
            ApplicationStorage.startPage = request;
            ApplicationStorage.getSettings().set("settings.startPage",request);
        } else if(request.startsWith("language.")) {
            request = request.replaceFirst("language.","");
            ApplicationStorage.language = request;
            ApplicationStorage.getSettings().set("settings.language",request);
        }
    }

    private void syncSettings(String request) {
        if(request.equals("general")) {
            String channel = "experimental"; boolean autoUpdate = false;
            if(ApplicationStorage.getUpdateSettings().getBoolean("updater.settings.autoUpdate")!=null) {
                autoUpdate = ApplicationStorage.getUpdateSettings().getBool("updater.settings.autoUpdate");
            }
            if(ApplicationStorage.getUpdateSettings().getString("updater.settings.updateChannel")!=null) {
                channel = ApplicationStorage.getUpdateSettings().getString("updater.settings.updateChannel");
            }
            if(ApplicationStorage.getOS().startsWith("Linux")) {
                boolean linuxCustomFrame = true;
                if (ApplicationStorage.getSettings().get("settings.linux.customFrame") != null) {
                    linuxCustomFrame = ApplicationStorage.getSettings().getBool("settings.linux.customFrame");
                }
                frame.executeJavaScript("document.getElementById('linux-settings-custom-frame').style.display = 'inherit'; linuxFrame = "+linuxCustomFrame+"; document.getElementById('linux-settings-enable-custom-frame').checked = linuxFrame;");
            }
            frame.executeJavaScript("updates = "+autoUpdate+"; document.getElementById('updater-settings-enable-updates').checked = updates; document.getElementById('updater-settings-update-channel').value = \""+channel+"\"; document.getElementById('updater-settings').style.display = 'inherit'; document.getElementById('general-settings-start-page').value = '"+ ApplicationStorage.startPage+"'; document.getElementById('updater-settings').style.display = 'inherit';");
        } else if(request.equals("about")) {
            frame.executeJavaScript("document.getElementById('settings-global-application-version').innerText = \""+ ApplicationStorage.getApplicationVersion()+"\"");
        }
    }

    private void syncDiscover(String request) {
        if(request.startsWith("search.")) {
            request = request.replace("search.","");
            if(request.startsWith("modules")) {
                String query;
                if(request.startsWith("modules.")) {
                    query = request.replaceFirst("modules.","");
                } else {
                    query = "";
                }
                ArrayList<ReadableModule> results = null;
                String searchTerm = "";
                if(moduleSearch.getCachedSearchTerm()!=null) {
                    searchTerm = moduleSearch.getCachedSearchTerm();
                }
                if(moduleSearch.getCachedResults()==null||!searchTerm.equals(query)) {
                    results = moduleSearch.search(query);
                }
                if(moduleSearch.getCachedSearchTerm()!=null) {
                    if(!moduleSearch.getCachedSearchTerm().isEmpty()&&!moduleSearch.getCachedSearchTerm().isBlank()) {
                        frame.executeJavaScript("document.getElementById(\"search-bar\").placeholder = \""+moduleSearch.getCachedSearchTerm()+"\";");
                    }
                }
                if(results == null) {
                    results = moduleSearch.search(query);
                }
                for(ReadableModule module : results) {
                    String tags = "Tags: "+module.getTagString();
                    String meta = module.getId()+" | v"+module.getVersion()+" | Hidden: "+module.isHidden()+"<br>"+tags;
                    String location = module.getLocation();
                    String actions = "<a onclick=\\\"connector('sync.discover.details.module."+location+"');\\\"><i class='bx bx-spreadsheet'></i> More</a> ";
                    if(NexusApplication.getModuleLoader().getModuleIds().contains(module.getId())) {
                        actions = "v"+NexusApplication.getModuleLoader().getModules().get(module.getId()).getVersion()+"  <a onclick=\\\"connector('sync.discover.details.module."+location+"');\\\"><i class='bx bx-spreadsheet'></i> More</a> <a style=\\\"background: #473e5c !important; color: white!important; cursor: not-allowed !important; box-shadow: 0 0 0.2rem var(--shadow3) !important;\\\"><i class='bx bx-check'></i> Installed</a>";
                    } else {
                        actions = actions+"<a style=\\\"background: #5632a8; color: white;\\\" onclick=\\\"connector('sync.discover.install.module.nexus-minecraft-module');\\\"><i class='bx bx-download'></i> Install</a>";
                    }
                    String command = "addResult(\""+module.getId()+"\",\""+module.getThumbnailUrl()+"\",\""+module.getName()+"\",\""+module.getAuthor()+"\",\""+formatForDetails(module.getSummary())+"\",\""+meta+"\",\""+actions+"\",\""+location+"\");";
                    frame.executeJavaScript(command);
                }
            }
        } else if(request.startsWith("details.")) {
            request = request.replaceFirst("details.","");
            if(request.startsWith("module.")) {
                ReadableModule module = new ReadableModule(URLDecoder.decode(request.replaceFirst("module.",""),StandardCharsets.UTF_8));
                frame.executeJavaScript("enableOverlay(\""+initDetails(module.getName(),module.getId(),"Application module",module.getVersion(),module.getSummary(),module.getAuthor(),module.isHidden(),module.getTagString(),module.getDescription(),module.getChangelogs().toString(),module.getVersions().toString(),module.getInfoText(),module.getInfoCard(),module.getBackgroundUrl(),module.getIconUrl(),module.getLogoUrl(),module.getThumbnailUrl())+"\");");
            }
        }
    }

    @SuppressWarnings("all")
    public static String initDetails(String name, String id, String type, String version, String summary, String authors, boolean isHidden, String tags, String description, String changelog, String versions, String customInfoHTML, String customInfoCardHTML, String background, String icon, String logo, String thumbnail) {
        String url = ApplicationStorage.urlBase+ ApplicationStorage.language+"/sub-details.html";
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
        }
        if(icon!=null) {
            url = url+"&icon="+formatForDetails(icon);
        }
        if(logo!=null) {
            url = url+"&logo="+formatForDetails(logo);
        }
        if(thumbnail!=null) {
            String t = "&thumbnail="+formatForDetails(thumbnail);
            url = url+t;
        }
        url = url.replace("\\","/");
        return url;
    }

    public static String formatForDetails(String input) {
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
