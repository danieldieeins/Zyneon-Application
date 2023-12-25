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
import live.nerotv.zyneon.app.application.frontend.language.Language;
import live.nerotv.zyneon.app.application.frontend.settings.MemoryWindow;
import org.cef.browser.CefBrowser;
import org.cef.handler.CefLoadHandler;
import org.cef.handler.CefLoadHandlerAdapter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.io.*;
import java.net.URI;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class BackendConnectorV3 implements BackendConnectorV2 {

    private final ZyneonWebFrame frame;

    public BackendConnectorV3(ZyneonWebFrame frame) {
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
        final File instances = new File(Main.getDirectoryPath()+"instances");
        for (final File instance:instances.listFiles()) {
            if(instance.isDirectory()) {
                File file = new File(instance.getPath()+"/zyneonInstance.json");
                if(file.exists()) {
                    Config config = new Config(file);
                    String id = config.getString("modpack.id");
                    String name = config.getString("modpack.name");
                    String version = config.getString("modpack.version");
                    String minecraft = config.getString("modpack.minecraft");
                    String modloader;
                    String mlversion="";
                    if(config.getString("modpack.forge.version")!=null) {
                        modloader = "Forge";
                        mlversion = config.getString("modpack.forge.version");
                    } else if(config.getString("modpack.fabric")!=null) {
                        modloader = "Fabric";
                        mlversion = config.getString("modpack.fabric");
                    } else {
                        modloader = "Vanilla";
                    }
                    frame.getBrowser().executeJavaScript("syncInstance(\""+id+"\",\""+name+"\",\""+version+"\",\""+modloader+"\",\""+mlversion+"\",\""+minecraft+"\",\""+Language.getShow()+"\")",frame.getBrowser().getURL(),1);
                }
            }
        }
    }

    private void listFilesForFolder(final File folder) {
        for (final File fileEntry : folder.listFiles()) {
            if (fileEntry.isDirectory()) {
                listFilesForFolder(fileEntry);
            } else {
                System.out.println(fileEntry.getName());
            }
        }
    }

    @Override
    public void resolveRequest(String request) {
        if (request.contains("button.minimize")) {
            frame.setState(Frame.ICONIFIED);
        } else if(request.contains("button.lightmode")) {
            frame.titlebar.setBackground(Color.decode("#ffffff"));
            frame.title.setForeground(Color.BLACK);
            frame.close.setForeground(Color.BLACK);
            frame.close.setBackground(Color.decode("#ededed"));
            frame.getBrowser().executeJavaScript("turnOnLights();", "https://danieldieeins.github.io/ZyneonApplicationContent/h/account.html", 5);
        } else if(request.contains("button.darkmode")) {
            frame.titlebar.setBackground(Color.decode("#03000b"));
            frame.title.setForeground(Color.WHITE);
            frame.close.setForeground(Color.WHITE);
            frame.close.setBackground(Color.BLACK);
            frame.getBrowser().executeJavaScript("turnOffLights();", "https://danieldieeins.github.io/ZyneonApplicationContent/h/account.html", 5);
        } else if(request.contains("button.refresh")) {
            frame.getBrowser().loadURL(Main.getDirectoryPath()+"libs/zyneon/"+Main.v+"/index.html");
        } else if(request.contains("button.close")) {
            SwingUtilities.invokeLater(() -> {
                frame.getInstance().dispatchEvent(new WindowEvent(frame.getInstance(), WindowEvent.WINDOW_CLOSING));
            });
        } else if(request.contains("button.instancesettings.")) {
            String id = request.replace("button.instancesettings.","").toLowerCase();
            File file = new File(Main.getDirectoryPath()+"instances/"+id+"/zyneonInstance.json");
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
            File file = new File(Main.getDirectoryPath()+"instances/"+id+"/zyneonInstance.json");
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
            File instance = new File(Main.getDirectoryPath()+"instances/"+name+"/");
            if(!instance.exists()) {
                instance.mkdirs();
                File file = new File(Main.getDirectoryPath()+"instances/"+name+"/zyneonInstance.json");
                Config config = new Config(file);
                config.set("modpack.id",name);
                config.set("modpack.name",name);
                config.set("modpack.version",1.0);
                config.set("modpack.minecraft",version);
                config.set("modpack.instance","instances/"+name+"/");
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
        } else if (request.contains("button.start.")) {
            System.out.println(request);
            resolveInstanceRequest(InstanceAction.RUN, request.replace("button.start.", ""));
        } else if (request.equalsIgnoreCase("button.starttab")) {
            if (Main.starttab.equalsIgnoreCase("start")) {
                Main.config.set("settings.starttab", "instances");
                Main.starttab = "instances";
            } else {
                Main.config.set("settings.starttab", "start");
                Main.starttab = "start";
            }
            frame.getBrowser().reload();
        } else if (request.equalsIgnoreCase("button.language")) {
            String lang = Main.config.getString("settings.language");
            if (lang.equalsIgnoreCase("auto")) {
                Main.config.set("settings.language", "german");
                Main.language = "german";
            } else if (lang.equalsIgnoreCase("german")) {
                Main.config.set("settings.language", "english");
                Main.language = "english";
            } else {
                Main.config.set("settings.language", "auto");
                if (System.getProperty("user.language").equalsIgnoreCase("de")) {
                    Main.language = "german";
                } else {
                    Main.language = "english";
                }
            }
            frame.getBrowser().reload();
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
            resolveInstanceRequest(InstanceAction.OPEN_FOLDER, request.replace("button.folder.", ""));
        } else if (request.contains("button.screenshots.")) {
            resolveInstanceRequest(InstanceAction.SHOW_SCREENSHOTS, request.replace("button.screenshots.", ""));
        } else if (request.contains("button.game.zyverse")) {
            startZyverse();
        } else if (request.contains("button.zyneonplus.")) {
            Main.config.set("settings.zyneonplus", request.replace("button.zyneonplus.", ""));
            frame.getBrowser().executeJavaScript("changeFrame(\"instances.html?version="+Main.config.getString("settings.zyneonplus")+"\");","",1);
        } else if (request.contains("button.zyneonplus")) {
            if (Main.config.getString("settings.zyneonplus") != null) {
                frame.getBrowser().executeJavaScript("changeFrame(\"instances.html?version="+Main.config.getString("settings.zyneonplus")+"\");","",1);
            } else {
                frame.getBrowser().executeJavaScript("changeFrame(\"instances.html?version=dynamic);", "", 1);
            }
        } else if (request.contains("button.resourcepacks.")) {
            resolveInstanceRequest(InstanceAction.SHOW_RESOURCEPACKS, request.replace("button.resourcepacks.", ""));
        } else if (request.contains("button.shaders.")) {
            resolveInstanceRequest(InstanceAction.SHOW_SHADERS, request.replace("button.shaders.", ""));
        } else if (request.contains("button.worlds.")) {
            resolveInstanceRequest(InstanceAction.SHOW_WORLDS, request.replace("button.worlds.", ""));
        } else if (request.contains("button.settings.")) {
            resolveInstanceRequest(InstanceAction.SETTINGS_MEMORY, request.replace("button.settings.", "").replace("memory", "default"));
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
                Application.auth.getSaveFile().delete();
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
            Language.syncLanguage();
            if (Application.auth.isLoggedIn()) {
                frame.getBrowser().executeJavaScript("javascript:login('"+Application.auth.getAuthInfos().getUsername()+"')", "https://danieldieeins.github.io/ZyneonApplicationContent/h/account.html", 5);
            } else {
                frame.getBrowser().executeJavaScript("javascript:logout()", "https://danieldieeins.github.io/ZyneonApplicationContent/h/account.html", 5);
            }
        } else if(request.equalsIgnoreCase("connector.profilesync")) {
            if(Application.auth.isLoggedIn()) {
                frame.getBrowser().executeJavaScript("javascript:syncProfilesAlt('" + Application.auth.getAuthInfos().getUsername() + "','" + Application.auth.getAuthInfos().getUuid() + "')", "https://danieldieeins.github.io/ZyneonApplicationContent/h/account.html", 5);
                Application.getFrame().getBrowser().executeJavaScript("javascript:syncLanguage('profiles.html#alt1','profiles.html#"+Application.auth.getAuthInfos().getUuid()+"')", "https://danieldieeins.github.io/ZyneonApplicationContent/h/account.html", 5);
            }
        } else {
            frame.getBrowser().executeJavaScript("javascript:OpenModal('notimplemented')", "https://a.nerotv.live/zyneon/application/html/account.html", 5);
            Main.getLogger().error("REQUEST NOT RESOLVED: " + request);
        }
    }

    private void syncInstance(String instance) {
        Main.getLogger().debug("REQUESTED INSTANCE SYNC: "+instance);
        if(new File(Main.getDirectoryPath()+"instances/"+instance+"/zyneonInstance.json").exists()) {
            Config config = new Config(Main.getDirectoryPath()+"instances/"+instance+"/zyneonInstance.json");
            Language.sync("%instance_version%",config.getString("modpack.version"));
            Language.sync("%instance_mcversion%",config.getString("modpack.minecraft"));
            if(config.getString("modpack.fabric")!=null) {
                Language.sync("instance_modloader","Fabric "+config.getString("modpack.fabric"));
            } else {
                Language.sync("instance_modloader", "Forge ("+config.getString("modpack.forge.type")+") "+config.getString("modpack.forge.version"));
            }
        } else {
            Language.sync("%instance_version%",Language.getNotInstalled());
            try {
                String url = "https://raw.githubusercontent.com/danieldieeins/ZyneonApplicationContent/main/m/" + instance + ".json";
                new File(Main.getDirectoryPath()+"temp/").mkdirs();
                Config json = new Config(FileUtils.downloadFile(url, Main.getDirectoryPath() + "temp/" + UUID.randomUUID() + ".json"));
                Language.sync("%instance_mcversion%",json.getString("modpack.minecraft"));
                if(json.getString("modpack.fabric")!=null) {
                    Language.sync("instance_modloader","Fabric "+json.getString("modpack.fabric"));
                } else {
                    Language.sync("instance_modloader", "Forge ("+json.getString("modpack.forge.type")+") "+json.getString("modpack.forge.version"));
                }
                json.getJsonFile().delete();
            } catch (Exception e) {
                Language.sync("%instance_mcversion%", "?");
                Language.sync("%instance_modloader%", "?");
            }
        }
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

    @Override
    @Deprecated
    public boolean startInstance(String s) {
        return runInstance(s);
    }

    @Override
    @Deprecated
    public boolean startZyneonPlus(String s, int r) {
        return startInstance(s);
    }

    public boolean runInstance(String instanceString) {
        if (instanceString.startsWith("official/")) {
            Config instanceJson;
            if (new File(Main.getDirectoryPath() + "instances/" + instanceString + "/zyneonInstance.json").exists()) {
                instanceJson = new Config(new File(Main.getDirectoryPath() + "instances/" + instanceString + "/zyneonInstance.json"));
            } else {
                new File(Main.getDirectoryPath() + "instances/" + instanceString + "/").mkdirs();
                String s = "https://raw.githubusercontent.com/danieldieeins/ZyneonApplicationContent/main/m/" + instanceString + ".json";
                File file = FileUtils.downloadFile(s, Main.getDirectoryPath() + "instances/" + instanceString + "/zyneonInstance.json");
                instanceJson = new Config(file);
            }
            if (instanceJson.getString("modpack.fabric") != null) {
                new FabricLauncher(frame).launch(new FabricInstance(instanceJson), Main.config.getInteger("settings.memory.default"));
            } else if (instanceJson.getString("modpack.forge.version") != null && instanceJson.getString("modpack.forge.type") != null) {
                new ForgeLauncher(frame).launch(new ForgeInstance(instanceJson), Main.config.getInteger("settings.memory.default"));
            } else {
                new VanillaLauncher(frame).launch(new VanillaInstance(instanceJson), Main.config.getInteger("settings.memory.default"));
            }
            return true;
        } else {
            File file = new File(Main.getDirectoryPath() + "instances/" + instanceString + "/zyneonInstance.json");
            if (file.exists()) {
                Config instanceJson = new Config(file);
                if (instanceJson.getString("modpack.fabric") != null) {
                    new FabricLauncher(frame).launch(new FabricInstance(instanceJson), Main.config.getInteger("settings.memory.default"));
                } else if (instanceJson.getString("modpack.forge.version") != null && instanceJson.getString("modpack.forge.type") != null) {
                    new ForgeLauncher(frame).launch(new ForgeInstance(instanceJson), Main.config.getInteger("settings.memory.default"));
                } else {
                    new VanillaLauncher(frame).launch(new VanillaInstance(instanceJson), Main.config.getInteger("settings.memory.default"));
                }
                return true;
            }
        }
        return false;
    }

    public void openInstanceFolder(String instance) {
        File folder = new File(Main.getDirectoryPath() + "instances/" + instance + "/");
        folder.mkdirs();
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

    private void openModsFolder(String instance) {
        File folder = new File(Main.getDirectoryPath() + "instances/" + instance + "/mods/");
        folder.mkdirs();
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

    private void openScreenshotsFolder(String instance) {
        File folder = new File(Main.getDirectoryPath() + "instances/" + instance + "/screenshots/");
        folder.mkdirs();
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

    private void openResourcePacksFolder(String instance) {
        File folder = new File(Main.getDirectoryPath() + "instances/" + instance + "/resourcepacks/");
        folder.mkdirs();
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

    private void openShadersFolder(String instance) {
        File folder = new File(Main.getDirectoryPath() + "instances/" + instance + "/shaderpacks/");
        folder.mkdirs();
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

    private void openWorldsFolder(String instance) {
        File folder = new File(Main.getDirectoryPath() + "instances/" + instance + "/saves/");
        folder.mkdirs();
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
        new MemoryWindow(Main.config, "RAM Einstellen (" + instance + ")", instance);
    }

    private void startZyverse() {
        Main.getLogger().debug("Downloading latest version...");
        try {
            InputStream inputStream = new BufferedInputStream(new URL("https://raw.githubusercontent.com/danieldieeins/ZyneonApplicationContent/main/z/latest.jar").openStream());
            new File(Main.getZyversePath()).mkdirs();
            FileOutputStream outputStream = new FileOutputStream(Main.getZyversePath() + "game.jar");
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            inputStream.close();
            outputStream.close();
            String path = URLDecoder.decode(Main.getZyversePath() + "game.jar", StandardCharsets.UTF_8);
            try {
                ProcessBuilder processBuilder = new ProcessBuilder("java", "-jar", path);
                processBuilder.redirectErrorStream(true);
                Process process = processBuilder.start();
                inputStream = process.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    Main.getLogger().debug(line);
                }
                int exitCode = process.waitFor();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
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