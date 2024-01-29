package live.nerotv.zyneon.app.application.backend.utils.backend.connector;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;
import fr.flowarg.flowupdater.versions.ForgeVersionType;
import live.nerotv.Main;
import live.nerotv.shademebaby.file.Config;
import live.nerotv.shademebaby.file.FileUtils;
import live.nerotv.zyneon.app.application.Application;
import live.nerotv.zyneon.app.application.backend.instance.FabricInstance;
import live.nerotv.zyneon.app.application.backend.instance.ForgeInstance;
import live.nerotv.zyneon.app.application.backend.instance.VanillaInstance;
import live.nerotv.zyneon.app.application.backend.launcher.FabricLauncher;
import live.nerotv.zyneon.app.application.backend.launcher.ForgeLauncher;
import live.nerotv.zyneon.app.application.backend.launcher.VanillaLauncher;
import live.nerotv.zyneon.app.application.backend.utils.FileUtil;
import live.nerotv.zyneon.app.application.backend.utils.frame.ZyneonWebFrame;
import live.nerotv.zyneon.app.application.frontend.settings.MemoryWindow;
import org.cef.browser.CefBrowser;
import org.cef.handler.CefLoadHandler;
import org.cef.handler.CefLoadHandlerAdapter;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

public class BackendConnector {

    private final ZyneonWebFrame frame;

    public BackendConnector(ZyneonWebFrame frame) {
        CefLoadHandler loadHandler = new CefLoadHandlerAdapter() {
            @Override
            public void onLoadingStateChange(CefBrowser browser, boolean isLoading, boolean canGoBack, boolean canGoForward) {
                if (!isLoading) {
                    URLLoadedEvent();
                }
            }
        };
        frame.getClient().addLoadHandler(loadHandler);
        this.frame = frame;
    }

    public void URLLoadedEvent() {
        if(frame.getBrowser().getURL().toLowerCase().endsWith("instances.html")) {
            loadInstances();
        }
    }

    public void loadInstances() {
        Main.getLogger().debug("loadInstances Request");
    }

    public static String addHyphensToUUID(String uuidString) {
        StringBuilder sb = new StringBuilder(uuidString);
        sb.insert(8, "-");
        sb.insert(13, "-");
        sb.insert(18, "-");
        sb.insert(23, "-");
        return sb.toString();
    }

    private void syncSettings(String type) {
        type = type.toLowerCase();
        switch (type) {
            case "general" -> {
                String tab = "start";
                if(Application.getStartURL().toLowerCase().contains("instances.html")) {
                    tab = "instances";
                }
                frame.executeJavaScript("syncGeneral('"+tab+"');");
            }
            case "global" -> {
                frame.executeJavaScript("syncGlobal('"+Main.config.getString("settings.memory.default").replace(".0","")+" MB','"+Main.getInstancePath()+"')");
            }
            case "profile" -> {
                if(Application.auth.isLoggedIn()) {
                    frame.executeJavaScript("syncProfile('"+Application.auth.getAuthInfos().getUsername()+"','"+addHyphensToUUID(Application.auth.getAuthInfos().getUuid())+"');");
                } else {
                    frame.executeJavaScript("syncLogin();");
                    frame.executeJavaScript("logout();");
                }
            }
            case "version" -> {
                frame.executeJavaScript("syncApp('"+Application.version+"');");
            }
        }
    }

    public void resolveRequest(String request) {
        if(request.contains("button.zyneondrive")) {
            if(request.contains(".web")) {
                if (Desktop.isDesktopSupported()) {
                    try {
                        Desktop.getDesktop().browse(URI.create("https://drive.zyneonstudios.com"));
                    } catch (IOException ignore) {}
                }
            } else {
                frame.getBrowser().loadURL("https://drive.zyneonstudios.com/app/index.html?tab=drive.html");
            }
        } else if(request.contains("load.")) {
            request = request.replace("load.","");
            frame.getBrowser().loadURL("file://"+Main.getDirectoryPath()+"libs/zyneon/"+Main.version +"/index.html?tab="+request);
        } else if (request.contains("button.minimize")) {
            frame.setState(Frame.ICONIFIED);
        } else if(request.equals("button.copy.uuid")) {
            StringSelection uuid = new StringSelection(addHyphensToUUID(Application.auth.getAuthInfos().getUuid()));
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(uuid,uuid);
        } else if(request.contains("sync.instances.list")) {
            Main.getInstancePath();
            String filePath = Main.getDirectoryPath()+"libs/zyneon/instances.json";
            Gson gson = new Gson();
            try (JsonReader reader = new JsonReader(new FileReader(filePath))) {
                JsonObject jsonObject = gson.fromJson(reader, JsonObject.class);
                JsonArray instances = jsonObject.getAsJsonArray("instances");
                for (JsonElement element : instances) {
                    JsonObject instance = element.getAsJsonObject();
                    String png = "assets/zyneon/images/instances/"+instance.get("id").toString().replace("\"","")+".png";
                    if(new File(Main.getDirectoryPath()+"libs/zyneon/"+Main.version+"/"+png).exists()) {
                        frame.executeJavaScript("addInstanceToList(" + instance.get("id") + "," + instance.get("name") + ",'" + png + "');");
                    } else {
                        frame.executeJavaScript("addInstanceToList(" + instance.get("id") + "," + instance.get("name") + ");");
                    }
                }
            } catch (IOException e) {
                Main.getLogger().error(e.getMessage());
            }
        } else if(request.contains("sync.login")) {
            if(Application.auth.isLoggedIn()) {
                frame.executeJavaScript("login('" + Application.auth.getAuthInfos().getUsername() + "');");
            } else {
                frame.executeJavaScript("logout();");
            }
        } else if(request.contains("sync.settings.")) {
            syncSettings(request.replace("sync.settings.",""));
        } else if(request.contains("button.theme.light")) {
            Application.theme = "light";
            Main.config.set("settings.appearance.theme",Application.theme);
            frame.setTitlebar("Zyneon Application",Color.white,Color.black);
        } else if(request.contains("button.theme.zyneon")) {
            Application.theme = "zyneon";
            Main.config.set("settings.appearance.theme",Application.theme);
            frame.setTitlebar("Zyneon Application",Color.decode("#050113"),Color.white);
        } else if(request.contains("button.theme.dark")) {
            Application.theme = "dark";
            Main.config.set("settings.appearance.theme",Application.theme);
            frame.setTitlebar("Zyneon Application",Color.black,Color.white);
        } else if(request.contains("button.connect")) {
            frame.getBrowser().loadURL("https://drive.zyneonstudios.com/app/index.html#");
        } else if(request.contains("button.refresh")) {
            if(request.contains(".instances")) {
                Application.loadInstances();
                frame.getBrowser().loadURL(Application.getInstancesURL());
            } else {
                frame.getBrowser().loadURL(Application.getStartURL());
            }
        } else if(request.contains("button.minecraftwiki")) {
            if (Desktop.isDesktopSupported()) {
                try {
                    Desktop.getDesktop().browse(URI.create("https://minecraft.wiki"));
                } catch (IOException ignore) {}
            }
        } else if(request.contains("button.exit")) {
            SwingUtilities.invokeLater(() -> {
                frame.getInstance().dispatchEvent(new WindowEvent(frame.getInstance(), WindowEvent.WINDOW_CLOSING));
            });
        } else if(request.contains("button.instancesettings.")) {
            String id = request.replace("button.instancesettings.","").toLowerCase();
            File file = new File(Main.getInstancePath()+"instances/"+id+"/zyneonInstance.json");
            if(file.exists()) {
                Config config = new Config(file);
                String modloader;
                if(config.getString("modpack.forge.version")!=null) {
                    modloader = "Forge";
                    String mlversion = config.getString("modpack.forge.version");
                    modloader = modloader+" "+mlversion;
                } else if(config.getString("modpack.fabric")!=null) {
                    modloader = "Fabric";
                    String mlversion = config.getString("modpack.fabric");
                    modloader = modloader+" "+mlversion;
                } else {
                    modloader = "Vanilla";
                }
                String instanceString = "instance-settings.html?instance=%name%&id=%id%&modloader=%20%modloader%&version=%minecraft%%20"
                        .replace("%name%",config.getString("modpack.name"))
                        .replace("%modloader%",modloader)
                        .replace("%minecraft%",config.getString("modpack.minecraft"))
                        .replace("%id%",config.getString("modpack.id"));
                frame.getBrowser().loadURL(Main.getDirectoryPath() + "libs/zyneon/" + Main.version + "/"+instanceString);
            }
        } else if (request.contains("button.instance.")) {
            String id = request.replace("button.instance.","").toLowerCase();
            File file = new File(Main.getInstancePath()+"instances/"+id+"/zyneonInstance.json");
            if(file.exists()) {
                Config instance = new Config(file);

                File icon = new File(Main.getDirectoryPath()+"libs/zyneon/"+Main.version+"/assets/zyneon/images/instances/"+id+".png");
                File logo = new File(Main.getDirectoryPath()+"libs/zyneon/"+Main.version+"/assets/zyneon/images/instances/"+id+"-logo.png");
                File background = new File(Main.getDirectoryPath()+"libs/zyneon/"+Main.version+"/assets/zyneon/images/instances/"+id+".webp");
                String icon_ = "";
                String logo_ = "";
                String background_ = "";
                if(icon.exists()) {
                    icon_ = "assets/zyneon/images/instances/"+id+".png";
                }
                if(logo.exists()) {
                    logo_ = "assets/zyneon/images/instances/"+id+"-logo.png";
                }
                if(background.exists()) {
                    background_ = "assets/zyneon/images/instances/"+id+".webp";
                }
                frame.executeJavaScript("syncTitle('"+instance.get("modpack.name")+"','"+icon_+"');");
                frame.executeJavaScript("syncLogo('"+logo_+"');");
                frame.executeJavaScript("syncBackground('"+background_+"');");
                String modloader = "Vanilla";
                String mlversion = "No mods";
                if(instance.getString("modpack.forge.version")!=null) {
                    modloader = "Forge";
                    mlversion = instance.getString("modpack.forge.version")+" ("+instance.getString("modpack.forge.type").toLowerCase()+")";
                } else if(instance.getString("modpack.fabric")!=null) {
                    modloader = "Fabric";
                    mlversion = instance.getString("modpack.fabric");
                }
                frame.executeJavaScript("syncDock('"+id+"','"+instance.get("modpack.version")+"','"+instance.get("modpack.minecraft")+"','"+modloader+"','"+mlversion+"');");
            }
        } else if (request.contains("button.delete.")) {
            request = request.replace("button.delete.","");
            File instance = new File(Main.getInstancePath()+"instances/"+request+"/");
            if(instance.exists()) {
                FileUtil.deleteFolder(instance);
                resolveRequest("button.refresh.instances");
            }
        } else if (request.contains("button.creator.create.")) {
            String[] creator = request.replace("button.creator.create.","").split("\\.", 5);
            String name = creator[0];
            name = name.replace("%DOT%",".");
            String version = creator[1];
            version = version.replace("%DOT%",".");
            String minecraft = creator[2];
            minecraft = minecraft.replace("%DOT%",".");
            String modloader = creator[3];
            String mlversion = creator[4];
            mlversion = mlversion.replace("%DOT%",".");
            String id = name.toLowerCase().replaceAll("[^a-z0-9]", "");
            File instancePath = new File(Main.getInstancePath()+"instances/"+id+"/");
            if(!instancePath.exists()) {
                Main.getLogger().debug("Created instance path: "+instancePath.mkdirs());
                Config instance = new Config(instancePath.getAbsolutePath()+"/zyneonInstance.json");
                instance.set("modpack.id",id);
                instance.set("modpack.name",name);
                instance.set("modpack.version",version);
                instance.set("modpack.minecraft",minecraft);
                if(modloader.equalsIgnoreCase("forge")) {
                    instance.set("modpack.forge.version",mlversion);
                    if(mlversion.toLowerCase().startsWith("old")) {
                        instance.set("modpack.forge.type", ForgeVersionType.OLD.toString());
                    } else if(mlversion.toLowerCase().startsWith("neo")) {
                        instance.set("modpack.forge.type", ForgeVersionType.NEO_FORGE.toString());
                    } else {
                        instance.set("modpack.forge.type", ForgeVersionType.NEW.toString());
                    }
                } else if(modloader.equalsIgnoreCase("fabric")) {
                    instance.set("modpack.fabric",mlversion);
                }
                instance.set("modpack.instance","instances/"+id+"/");
            }
            resolveRequest("button.refresh.instances");
        } else if (request.contains("button.start.")) {
            Main.getLogger().debug("Trying to start instance "+request.replace("button.start.",""));
            resolveInstanceRequest(InstanceAction.RUN, request.replace("button.start.", ""));
        } else if (request.contains("button.starttab.")) {
            String tab = request.replace("button.starttab.","");
            if(tab.equalsIgnoreCase("instances")) {
                Main.config.set("settings.starttab", "instances");
                Main.starttab = "instances";
            } else {
                Main.config.set("settings.starttab", "start");
                Main.starttab = "start";
            }
            syncSettings("general");
        } else if (request.equalsIgnoreCase("button.username")) {
            if (Desktop.isDesktopSupported()) {
                try {
                    Desktop.getDesktop().browse(URI.create("https://www.minecraft.net/de-de/msaprofile/mygames/editprofile"));
                } catch (IOException ignore) {
                }
            }
        } else if (request.equalsIgnoreCase("button.instances")) {
            frame.getBrowser().loadURL(Main.getDirectoryPath() + "libs/zyneon/" + Main.version + "/instances.html");
        } else if (request.equalsIgnoreCase("button.skin")) {
            if (Desktop.isDesktopSupported()) {
                try {
                    Desktop.getDesktop().browse(URI.create("https://www.minecraft.net/de-de/msaprofile/mygames/editskin"));
                } catch (IOException ignore) {
                }
            }
        } else if (request.equalsIgnoreCase("button.website")) {
            if (Application.auth.isLoggedIn()) {
                if (Desktop.isDesktopSupported()) {
                    try {
                        Desktop.getDesktop().browse(URI.create("https://www.zyneonstudios.com/home"));
                    } catch (IOException ignore) {
                    }
                }
            } else {
                Application.login();
                SwingUtilities.invokeLater(() -> {
                    Application.auth.startAsyncWebview();
                });
            }
        } else if (request.equalsIgnoreCase("button.discord")) {
            if (Application.auth.isLoggedIn()) {
                if (Desktop.isDesktopSupported()) {
                    try {
                        Desktop.getDesktop().browse(URI.create("https://discord.gg/Q2AEWfesZW"));
                    } catch (IOException ignore) {
                    }
                }
            } else {
                Application.login();
                SwingUtilities.invokeLater(() -> {
                    Application.auth.startAsyncWebview();
                });
            }
        } else if (request.equalsIgnoreCase("button.laby")) {
            if (Application.auth.isLoggedIn()) {
                if (Desktop.isDesktopSupported()) {
                    try {
                        Desktop.getDesktop().browse(URI.create("https://laby.net/@" + Application.auth.getAuthInfos().getUsername()));
                    } catch (IOException ignore) {
                    }
                }
            } else {
                Application.login();
                SwingUtilities.invokeLater(() -> {
                    Application.auth.startAsyncWebview();
                });
            }
        } else if (request.contains("button.mods.")) {
            resolveInstanceRequest(InstanceAction.SHOW_MODS, request.replace("button.mods.", ""));
        } else if (request.contains("button.folder.")) {
            if(request.equals("button.folder.instances")) {
                resolveInstanceRequest(InstanceAction.OPEN_FOLDER,"");
            } else {
                resolveInstanceRequest(InstanceAction.OPEN_FOLDER, request.replace("button.folder.", ""));
            }
        } else if (request.contains("button.screenshots.")) {
            resolveInstanceRequest(InstanceAction.SHOW_SCREENSHOTS, request.replace("button.screenshots.", ""));
        } else if (request.contains("button.zyneonplus.")) {
            Main.config.set("settings.zyneonplus", request.replace("button.zyneonplus.", ""));
            frame.getBrowser().executeJavaScript("changeFrame(\"instances.html?version="+Main.config.getString("settings.zyneonplus")+"\");","",1);
        } else if (request.contains("button.zyneonplus")) {
            if (Main.config.getString("settings.zyneonplus") != null) {
                frame.getBrowser().executeJavaScript("changeFrame(\"instances.html?version="+Main.config.getString("settings.zyneonplus")+"\");","",1);
            } else {
                frame.getBrowser().executeJavaScript("changeFrame(\"instances.html?version=dynamic\");", "", 1);
            }
        } else if (request.contains("button.install.")) {
            String id = request.replace("button.install.","");
            String url = "https://raw.githubusercontent.com/danieldieeins/ZyneonApplicationContent/main/m/" + id + ".json";
            File instance = new File(Main.getInstancePath()+"instances/"+id+"/");
            Main.getLogger().debug("Created instance path: "+instance.mkdirs());
            FileUtils.downloadFile(url,URLDecoder.decode(instance.getAbsolutePath()+"/zyneonInstance.json",StandardCharsets.UTF_8));
            resolveRequest("button.refresh.instances");
        } else if (request.contains("button.resourcepacks.")) {
            resolveInstanceRequest(InstanceAction.SHOW_RESOURCEPACKS, request.replace("button.resourcepacks.", ""));
        } else if (request.contains("button.shaders.")) {
            resolveInstanceRequest(InstanceAction.SHOW_SHADERS, request.replace("button.shaders.", ""));
        } else if (request.contains("button.worlds.")) {
            resolveInstanceRequest(InstanceAction.SHOW_WORLDS, request.replace("button.worlds.", ""));
        } else if (request.contains("button.settings.")) {
            resolveInstanceRequest(InstanceAction.SETTINGS_MEMORY, request.replace("button.settings.", "").replace("memory", "default"));
        } else if(request.contains("button.path.")) {
            request = request.replace("button.path.","").toLowerCase();
            if(request.equals("instances")) {
                SwingUtilities.invokeLater(() -> {
                    JFileChooser chooser = getJFileChooser();
                    int answer = chooser.showOpenDialog(null);
                    if(answer == JFileChooser.APPROVE_OPTION) {
                        String instancesPath = URLDecoder.decode(chooser.getSelectedFile().getAbsolutePath().replace("\\","/"), StandardCharsets.UTF_8);
                        Main.config.set("settings.path.instances",instancesPath);
                        if(!instancesPath.toLowerCase().contains("zyneon")) {
                            instancesPath=instancesPath+"/Zyneon";
                        }
                        Main.instances = instancesPath;
                        Application.loadInstances();
                        frame.getBrowser().loadURL(Application.getSettingsURL()+"&tab=global");
                    }
                });
            }
        } else if (request.contains("button.account")) {
            if (Application.auth.isLoggedIn()) {
                resolveRequest("button.logout");
                frame.getBrowser().reload();
                return;
            }
            Application.login();
            SwingUtilities.invokeLater(() -> {
                Application.auth.startAsyncWebview();
            });
        } else if (request.contains("button.logout")) {
            if (Application.auth.isLoggedIn()) {
                Main.getLogger().debug("Deleted login: "+Application.auth.getSaveFile().delete());
                Application.login();
            }
        } else if (request.contains("connector.sync")) {
            if (request.contains("instance")) {
                syncInstance(request.replace("connector.syncinstance.", ""));
                return;
            }
            if (Application.auth.isLoggedIn()) {
                frame.getBrowser().executeJavaScript("javascript:login('"+Application.auth.getAuthInfos().getUsername()+"')", "https://danieldieeins.github.io/ZyneonApplicationContent/h/account.html", 5);
                frame.getBrowser().executeJavaScript("javascript:drive('"+Application.auth.getAuthInfos().getUuid().replace("-","")+"')", "https://danieldieeins.github.io/ZyneonApplicationContent/h/account.html", 5);
            } else {
                frame.getBrowser().executeJavaScript("javascript:logout()", "https://danieldieeins.github.io/ZyneonApplicationContent/h/account.html", 5);
            }
        } else if(request.equalsIgnoreCase("connector.profilesync")) {
            if(Application.auth.isLoggedIn()) {
                frame.getBrowser().executeJavaScript("javascript:syncProfilesAlt('" + Application.auth.getAuthInfos().getUsername() + "','" + Application.auth.getAuthInfos().getUuid() + "')", "https://danieldieeins.github.io/ZyneonApplicationContent/h/account.html", 5);
                Application.getFrame().getBrowser().executeJavaScript("javascript:syncLanguage('profiles.html#alt1','profiles.html#"+Application.auth.getAuthInfos().getUuid()+"')", "https://danieldieeins.github.io/ZyneonApplicationContent/h/account.html", 5);
            }
        } else {
            Main.getLogger().error("REQUEST NOT RESOLVED: " + request);
        }
    }

    private static JFileChooser getJFileChooser() {
        JFileChooser chooser;
        try {
            chooser = new JFileChooser(Main.getInstancePath());
        } catch (Exception ignore) {
            chooser = new JFileChooser(Main.getDirectoryPath());
        }
        chooser.setDialogTitle("Select instances installation path");
        chooser.setMultiSelectionEnabled(false);
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setAcceptAllFileFilterUsed(false);
        FileNameExtensionFilter standardFilter = new FileNameExtensionFilter("Folders only", "*.*");
        chooser.addChoosableFileFilter(standardFilter);
        return chooser;
    }

    private void syncInstance(String instance) {
        Main.getLogger().debug("REQUESTED INSTANCE SYNC: "+instance);
    }

    public void resolveInstanceRequest(InstanceAction action, String instance) {
        switch (action) {
            case RUN -> runInstance(instance);
            case OPEN_FOLDER -> openInstanceFolder(instance);
            case SHOW_SCREENSHOTS -> openScreenshotsFolder(instance);
            case SHOW_MODS -> openModsFolder(instance);
            case SHOW_RESOURCEPACKS -> openResourcePacksFolder(instance);
            case SHOW_WORLDS -> openWorldsFolder(instance);
            case SHOW_SHADERS -> openShadersFolder(instance);
            case SETTINGS_MEMORY -> openMemorySettings(instance);
        }
    }

    public void runInstance(String instanceString) {
        if(!Application.auth.isLoggedIn()) {
            syncSettings("profile");
        }
        if (instanceString.startsWith("official/")) {
            Config instanceJson;
            if (new File(Main.getInstancePath() + "instances/" + instanceString + "/zyneonInstance.json").exists()) {
                instanceJson = new Config(new File(Main.getInstancePath() + "instances/" + instanceString + "/zyneonInstance.json"));
            } else {
                Main.getLogger().debug("Created instance path: "+new File(Main.getInstancePath() + "instances/" + instanceString + "/").mkdirs());
                String s = "https://raw.githubusercontent.com/danieldieeins/ZyneonApplicationContent/main/m/" + instanceString + ".json";
                File file = FileUtils.downloadFile(s, Main.getInstancePath() + "instances/" + instanceString + "/zyneonInstance.json");
                instanceJson = new Config(file);
            }
            launch(instanceJson);
        } else {
            File file = new File(Main.getInstancePath() + "instances/" + instanceString + "/zyneonInstance.json");
            if (file.exists()) {
                Config instanceJson = new Config(file);
                launch(instanceJson);
            }
        }
    }

    private void launch(Config instanceJson) {
        if (instanceJson.getString("modpack.fabric") != null) {
            new FabricLauncher(frame).launch(new FabricInstance(instanceJson), Main.config.getInteger("settings.memory.default"));
        } else if (instanceJson.getString("modpack.forge.version") != null && instanceJson.getString("modpack.forge.type") != null) {
            new ForgeLauncher(frame).launch(new ForgeInstance(instanceJson), Main.config.getInteger("settings.memory.default"));
        } else {
            new VanillaLauncher(frame).launch(new VanillaInstance(instanceJson), Main.config.getInteger("settings.memory.default"));
        }
    }

    public void openInstanceFolder(String instance) {
        if(instance==null) {
            instance = "";
        }
        File folder;
        if(instance.isEmpty()) {
            folder = new File(Main.getInstancePath() + "instances/");
        } else {
            folder = new File(Main.getInstancePath() + "instances/" + instance + "/");
        }
        createIfNotExist(folder);
    }

    private void openModsFolder(String instance) {
        File folder = new File(Main.getInstancePath() + "instances/" + instance + "/mods/");
        createIfNotExist(folder);
    }

    private void openScreenshotsFolder(String instance) {
        File folder = new File(Main.getInstancePath() + "instances/" + instance + "/screenshots/");
        createIfNotExist(folder);
    }

    private void openResourcePacksFolder(String instance) {
        File folder = new File(Main.getInstancePath() + "instances/" + instance + "/resourcepacks/");
        createIfNotExist(folder);
    }

    private void openShadersFolder(String instance) {
        File folder = new File(Main.getInstancePath() + "instances/" + instance + "/shaderpacks/");
        createIfNotExist(folder);
    }

    private void openWorldsFolder(String instance) {
        File folder = new File(Main.getInstancePath() + "instances/" + instance + "/saves/");
        createIfNotExist(folder);
    }

    private void createIfNotExist(File folder) {
        Main.getLogger().debug("Created instance path: "+folder.mkdirs());
        if (folder.exists()) {
            if (Desktop.isDesktopSupported()) {
                Desktop desktop = Desktop.getDesktop();
                if (desktop.isSupported(Desktop.Action.OPEN)) {
                    try {
                        desktop.open(folder);
                    } catch (Exception ignore) {
                    }
                }
            }
        }
    }

    private void openMemorySettings(String instance) {
        new MemoryWindow(Main.config, "Configure memory (" + instance + ")", instance);
    }

    public enum InstanceAction {
        RUN,
        OPEN_FOLDER,
        SHOW_SCREENSHOTS,
        SHOW_MODS,
        SHOW_RESOURCEPACKS,
        SHOW_SHADERS,
        SHOW_WORLDS,
        SETTINGS_MEMORY
    }
}