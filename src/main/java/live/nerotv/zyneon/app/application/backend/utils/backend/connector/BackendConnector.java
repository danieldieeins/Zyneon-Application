package live.nerotv.zyneon.app.application.backend.utils.backend.connector;

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
import live.nerotv.zyneon.app.application.backend.utils.frame.ZyneonWebFrame;
import live.nerotv.zyneon.app.application.frontend.settings.MemoryWindow;
import org.cef.browser.CefBrowser;
import org.cef.handler.CefLoadHandler;
import org.cef.handler.CefLoadHandlerAdapter;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.WindowEvent;
import java.io.File;
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

    private String theme = "dark";

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
            case "general" ->
                    frame.getBrowser().executeJavaScript("changeFrame('settings.html?tab=start.html&general-tab=" + Main.starttab + "&general-theme=" + theme + "');", "https://danieldieeins.github.io/ZyneonApplicationContent/h/account.html", 5);
            case "global" ->
                    frame.getBrowser().executeJavaScript("changeFrame('settings.html?tab=global.html&memory=" + Main.config.getInteger("settings.memory.default") + "&path=" + Main.getInstancePath().replace(":/", ":") + "');", "https://danieldieeins.github.io/ZyneonApplicationContent/h/account.html", 5);
            case "profile" -> {
                if (Application.auth.isLoggedIn()) {
                    frame.getBrowser().executeJavaScript("changeFrame('settings.html?tab=profile.html&username=" + Application.auth.getAuthInfos().getUsername() + "&uuid=" + addHyphensToUUID(Application.auth.getAuthInfos().getUuid()) + "');", "https://danieldieeins.github.io/ZyneonApplicationContent/h/account.html", 5);
                } else {
                    frame.getBrowser().executeJavaScript("changeFrame('settings.html?tab=login.html');", "https://danieldieeins.github.io/ZyneonApplicationContent/h/account.html", 5);
                }
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
            frame.getBrowser().loadURL("file://"+Main.getDirectoryPath()+"libs/zyneon/"+Main.v+"/index.html?tab="+request);
        } else if (request.contains("button.minimize")) {
            frame.setState(Frame.ICONIFIED);
        } else if(request.equals("button.copy.uuid")) {
            StringSelection uuid = new StringSelection(addHyphensToUUID(Application.auth.getAuthInfos().getUuid()));
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(uuid,uuid);
        } else if(request.contains("sync.settings.")) {
            syncSettings(request.replace("sync.settings.",""));
        } else if(request.contains("button.lightmode")) {
            theme = "light";
            frame.titlebar.setBackground(Color.decode("#ffffff"));
            frame.title.setForeground(Color.BLACK);
            frame.close.setForeground(Color.BLACK);
            frame.close.setBackground(Color.decode("#ededed"));
            frame.getBrowser().executeJavaScript("turnOnLights();", "https://danieldieeins.github.io/ZyneonApplicationContent/h/account.html", 5);
            syncSettings("general");
        } else if(request.contains("button.darkmode")) {
            theme = "dark";
            frame.titlebar.setBackground(Color.decode("#03000b"));
            frame.title.setForeground(Color.decode("#999999"));
            frame.close.setForeground(Color.WHITE);
            frame.close.setBackground(Color.BLACK);
            frame.getBrowser().executeJavaScript("turnOffLights();", "https://danieldieeins.github.io/ZyneonApplicationContent/h/account.html", 5);
            syncSettings("general");
        } else if(request.contains("button.connect")) {
            frame.getBrowser().loadURL("https://drive.zyneonstudios.com/app/index.html#");
        } else if(request.contains("button.refresh")) {
            frame.getBrowser().reload();
        } else if(request.contains("button.minecraftwiki")) {
            if (Desktop.isDesktopSupported()) {
                try {
                    Desktop.getDesktop().browse(URI.create("https://minecraft.wiki"));
                } catch (IOException ignore) {}
            }
        } else if(request.contains("button.close")) {
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
                frame.getBrowser().loadURL(Main.getDirectoryPath() + "libs/zyneon/" + Main.v + "/"+instanceString);
            }
        } else if (request.contains("modrinth")) {
            if (request.contains(".install.mod.")) {
                System.out.println(request);
                return;
            }
            System.out.println(request);
            Desktop desktop = Desktop.getDesktop();
            try {
                desktop.browse(new URI("https://modrinth.com/mod/" + request.replace("button.show.modrinth.", "")));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else if (request.contains("button.instance.")) {
            String id = request.replace("button.instance.","").toLowerCase();
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
                String instanceString = "instance.html?instance=%name%&id=%id%&modloader=%20%modloader%&version=%minecraft%%20"
                        .replace("%name%",config.getString("modpack.name"))
                        .replace("%modloader%",modloader)
                        .replace("%minecraft%",config.getString("modpack.minecraft"))
                        .replace("%id%",config.getString("modpack.id"));
                frame.getBrowser().loadURL(Main.getDirectoryPath() + "libs/zyneon/" + Main.v + "/"+instanceString);
            }
        } else if (request.contains("button.ic.start")) {
            frame.getBrowser().loadURL(Main.getDirectoryPath() + "libs/zyneon/" + Main.v + "/"+"creator.html");
        } else if (request.contains("button.creator.create.")) {
            String[] creator = request.replace("button.creator.create.","").split("\\.", 2);
            String name = creator[0];
            String version = creator[1];
            File instance = new File(Main.getInstancePath()+"instances/"+name+"/");
            if(!instance.exists()) {
                Main.getLogger().debug("Created instance path: "+instance.mkdirs());
                String instanceString = getInstanceString(name, version);
                frame.getBrowser().loadURL(Main.getDirectoryPath() + "libs/zyneon/" + Main.v + "/"+instanceString);
            }
        } else if (request.contains("button.start.")) {
            System.out.println(request);
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
            frame.getBrowser().loadURL(Main.getDirectoryPath() + "libs/zyneon/" + Main.v + "/instances.html");
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
                        frame.getBrowser().executeJavaScript("changeFrame('settings.html?tab=global.html&memory="+Main.config.getInteger("settings.memory.default")+"&path="+Main.getInstancePath().replace(":/",":")+"');", "https://danieldieeins.github.io/ZyneonApplicationContent/h/account.html", 5);
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
                if (Application.auth.isLoggedIn()) {
                    frame.setTitle("Zyneon Application (" + Application.getVersion() + ", " + Application.auth.getAuthInfos().getUsername() + ")");
                } else {
                    frame.setTitle("Zyneon Application (" + Application.getVersion() + ")");
                }
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

    @NotNull
    private static String getInstanceString(String name, String version) {
        File file = new File(Main.getInstancePath()+"instances/"+ name +"/zyneonInstance.json");
        Config config = new Config(file);
        config.set("modpack.id", name);
        config.set("modpack.name", name);
        config.set("modpack.version",1.0);
        config.set("modpack.minecraft", version);
        config.set("modpack.instance","instances/"+ name +"/");
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
        return "instance.html?instance=%name%&id=%id%&modloader=%20%modloader%&version=%minecraft%%20"
                .replace("%name%",config.getString("modpack.name"))
                .replace("%modloader%",modloader)
                .replace("%minecraft%",config.getString("modpack.minecraft"))
                .replace("%id%",config.getString("modpack.id"));
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