package live.nerotv.zyneon.app.application.backend.utils.backend.connector;

import live.nerotv.Main;
import live.nerotv.openlauncherapi.auth.SimpleMicrosoftAuth;
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

import java.awt.*;
import java.io.*;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class BackendConnectorV3 implements BackendConnectorV2 {

    private SimpleMicrosoftAuth auth;
    private final ZyneonWebFrame frame;

    public BackendConnectorV3(SimpleMicrosoftAuth auth, ZyneonWebFrame frame) {
        this.auth = auth;
        this.frame = frame;
    }

    @Override
    public void resolveRequest(String request) {
        if(request.contains("button.start.")) {
            resolveInstanceRequest(InstanceAction.RUN,request.replace("button.start.",""));
        } else if(request.contains("button.screenshots.")) {
            resolveInstanceRequest(InstanceAction.SHOW_SCREENSHOTS,request.replace("button.screenshots.",""));
        } else if(request.contains("button.game.zyverse")) {
            startZyverse();
        } else if(request.contains("button.zyneonplus.")) {
            Main.config.set("settings.zyneonplus",request.replace("button.zyneonplus.",""));
            frame.getBrowser().loadURL(Main.getDirectoryPath()+"libs/zyneon/"+Main.v+"/zyneonplus"+Main.config.getString("settings.zyneonplus")+".html");
        } else if(request.contains("button.zyneonplus")) {
            if(Main.config.getString("settings.zyneonplus")!=null) {
                String zyn = Main.config.getString("settings.zyneonplus");
                if (zyn.contains("1202")) {
                    frame.getBrowser().loadURL(Main.getDirectoryPath()+"libs/zyneon/"+Main.v+"/zyneonplus1202.html");
                    return;
                } else if (zyn.contains("1201")) {
                    frame.getBrowser().loadURL(Main.getDirectoryPath()+"libs/zyneon/"+Main.v+"/zyneonplus1201.html");
                    return;
                } else if (zyn.contains("1194")) {
                    frame.getBrowser().loadURL(Main.getDirectoryPath()+"libs/zyneon/"+Main.v+"/zyneonplus1194.html");
                    return;
                } else if (zyn.contains("1182")) {
                    frame.getBrowser().loadURL(Main.getDirectoryPath()+"libs/zyneon/"+Main.v+"/zyneonplus1182.html");
                    return;
                } else if (zyn.contains("1171")) {
                    frame.getBrowser().loadURL(Main.getDirectoryPath()+"libs/zyneon/"+Main.v+"/zyneonplus1171.html");
                    return;
                } else if (zyn.contains("1165")) {
                    frame.getBrowser().loadURL(Main.getDirectoryPath()+"libs/zyneon/"+Main.v+"/zyneonplus1165.html");
                    return;
                }
            }
            frame.getBrowser().loadURL(Main.getDirectoryPath()+"libs/zyneon/"+Main.v+"/zyneonplusversions.html");
        } else if(request.contains("button.resourcepacks.")) {
            resolveInstanceRequest(InstanceAction.SHOW_RESOURCEPACKS,request.replace("button.resourcepacks.",""));
        } else if(request.contains("button.shaders.")) {
            resolveInstanceRequest(InstanceAction.SHOW_SHADERS,request.replace("button.shaders.",""));
        } else if(request.contains("button.worlds.")) {
            resolveInstanceRequest(InstanceAction.SHOW_WORLDS, request.replace("button.worlds.", ""));
        } else if(request.contains("button.settings.")) {
            resolveInstanceRequest(InstanceAction.SETTINGS_MEMORY, request.replace("button.settings.", "").replace("memory","default"));
        } else if(request.contains("button.account")) {
            if(auth.isLoggedIn()) {
                resolveRequest("button.logout");
                frame.getBrowser().reload();
                return;
            }
            auth = Application.getNewAuth();
            auth.startAsyncWebview();
        } else if(request.contains("button.logout")) {
            if (auth.isLoggedIn()) {
                auth.getSaveFile().delete();
                auth = Application.getNewAuth();
                Application.login();
                if (auth.isLoggedIn()) {
                    frame.setTitle("Zyneon Application (" + Application.getVersion() + ", " + auth.getAuthInfos().getUsername() + ")");
                } else {
                    frame.setTitle("Zyneon Application (" + Application.getVersion() + ")");
                }
            }
        } else if(request.contains("connector.sync")) {
            syncLanguage();
            if(auth.isLoggedIn()) {
                frame.getBrowser().executeJavaScript("javascript:syncAccount('"+auth.getAuthInfos().getUsername()+"')", "https://danieldieeins.github.io/ZyneonApplicationContent/h/account.html", 5);
                frame.getBrowser().executeJavaScript("javascript:syncButton('Abmelden')", "https://danieldieeins.github.io/ZyneonApplicationContent/h/account.html", 5);
            } else {
                frame.getBrowser().executeJavaScript("javascript:syncAccount('"+Main.language.getString("account.notLoggedIn")+"')", "https://danieldieeins.github.io/ZyneonApplicationContent/h/account.html", 5);
                frame.getBrowser().executeJavaScript("javascript:syncButton('Anmelden')", "https://danieldieeins.github.io/ZyneonApplicationContent/h/account.html", 5);
            }
        } else {
            Main.getLogger().debug("REQUEST: "+request);
        }
    }

    public void resolveInstanceRequest(InstanceAction action, String instance) {
        switch (action) {
            case RUN -> runInstance(instance);
            case OPEN_FOLDER -> openInstanceFolder(instance);
            case SHOW_SCREENSHOTS -> openScreenshotsFolder(instance);
            case SHOW_RESOURCEPACKS -> openResourcePacksFolder(instance);
            case SHOW_WORLDS -> openWorldsFolder(instance);
            case SHOW_SHADERS -> openShadersFolder(instance);
            case SETTINGS_MEMORY -> openMemorySettings(instance);
        }
    }

    @Override @Deprecated
    public boolean startInstance(String s) {
        return runInstance(s);
    }

    @Override @Deprecated
    public boolean startZyneonPlus(String s, int r) {
        return startInstance(s);
    }

    public boolean runInstance(String instanceString) {
        if (instanceString.startsWith("official/")) {
            Config instanceJson;
            if (new File(Main.getDirectoryPath() + "instances/" + instanceString + "/zyneonInstance.json").exists()) {
                instanceJson = new Config(new File(Main.getDirectoryPath() + "instances/" + instanceString + "/zyneonInstance.json"));
            } else {
                new File(Main.getDirectoryPath()+"instances/"+instanceString+"/").mkdirs();
                File file = FileUtils.downloadFile("https://raw.githubusercontent.com/danieldieeins/ZyneonApplicationContent/main/m/"+instanceString+".json",Main.getDirectoryPath()+"instances/"+instanceString+"/zyneonInstance.json");
                instanceJson = new Config(file);
            }
            if (instanceJson.getString("modpack.fabric") != null) {
                new FabricLauncher(auth, frame).launch(new FabricInstance(instanceJson), Main.config.getInteger("settings.memory.default"));
            } else if (instanceJson.getString("modpack.forge.version") != null && instanceJson.getString("modpack.forge.type") != null) {
                new ForgeLauncher(auth, frame).launch(new ForgeInstance(instanceJson), Main.config.getInteger("settings.memory.default"));
            } else {
                new VanillaLauncher(auth, frame).launch(new VanillaInstance(instanceJson), Main.config.getInteger("settings.memory.default"));
            }
            return true;
        } else {
            if (new File(Main.getDirectoryPath() + "instances/" + instanceString + "/zyneonInstance.json").exists()) {
                Config instanceJson = new Config(new File(Main.getDirectoryPath() + "instances/" + instanceString + "/zyneonInstance.json"));
                if (instanceJson.getString("modpack.fabric") != null) {
                    new FabricLauncher(auth, frame).launch(new FabricInstance(instanceJson), Main.config.getInteger("settings.memory.default"));
                } else if (instanceJson.getString("modpack.forge.version") != null && instanceJson.getString("modpack.forge.type") != null) {
                    new ForgeLauncher(auth, frame).launch(new ForgeInstance(instanceJson), Main.config.getInteger("settings.memory.default"));
                } else {
                    new VanillaLauncher(auth, frame).launch(new VanillaInstance(instanceJson), Main.config.getInteger("settings.memory.default"));
                }
                return true;
            }
        }
        return false;
    }

    public void openInstanceFolder(String instance) {
        File folder = new File(Main.getDirectoryPath()+"instances/"+instance+"/");
        folder.mkdirs();
        if(folder.exists()) {
            if(Desktop.isDesktopSupported()) {
                Desktop desktop = Desktop.getDesktop();
                if(desktop.isSupported(Desktop.Action.OPEN)) {
                    try {
                        desktop.open(folder);
                    } catch (Exception ignore) {}
                }
            }
        }
    }

    private void openScreenshotsFolder(String instance) {
        File folder = new File(Main.getDirectoryPath()+"instances/"+instance+"/screenshots/");
        folder.mkdirs();
        if(folder.exists()) {
            if(Desktop.isDesktopSupported()) {
                Desktop desktop = Desktop.getDesktop();
                if(desktop.isSupported(Desktop.Action.OPEN)) {
                    try {
                        desktop.open(folder);
                    } catch (Exception ignore) {}
                }
            }
        }
    }

    private void openResourcePacksFolder(String instance) {
        File folder = new File(Main.getDirectoryPath()+"instances/"+instance+"/resourcepacks/");
        folder.mkdirs();
        if(folder.exists()) {
            if(Desktop.isDesktopSupported()) {
                Desktop desktop = Desktop.getDesktop();
                if(desktop.isSupported(Desktop.Action.OPEN)) {
                    try {
                        desktop.open(folder);
                    } catch (Exception ignore) {}
                }
            }
        }
    }

    private void openShadersFolder(String instance) {
        File folder = new File(Main.getDirectoryPath()+"instances/"+instance+"/shaderpacks/");
        folder.mkdirs();
        if(folder.exists()) {
            if(Desktop.isDesktopSupported()) {
                Desktop desktop = Desktop.getDesktop();
                if(desktop.isSupported(Desktop.Action.OPEN)) {
                    try {
                        desktop.open(folder);
                    } catch (Exception ignore) {}
                }
            }
        }
    }

    private void openWorldsFolder(String instance) {
        File folder = new File(Main.getDirectoryPath()+"instances/"+instance+"/saves/");
        folder.mkdirs();
        if(folder.exists()) {
            if(Desktop.isDesktopSupported()) {
                Desktop desktop = Desktop.getDesktop();
                if(desktop.isSupported(Desktop.Action.OPEN)) {
                    try {
                        desktop.open(folder);
                    } catch (Exception ignore) {}
                }
            }
        }
    }

    private void openMemorySettings(String instance) {
        new MemoryWindow(Main.config,"RAM Einstellen ("+instance+")",instance);
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

    private void syncLanguage() {
        ArrayList<String> strings = new ArrayList<>();
        strings.add("%start%");
        strings.add("%profile%");
        strings.add("%zyneonplus_button%");
        strings.add("%zyneonplus_text%");
        strings.add("%installing_title%");
        strings.add("%installing_text%");
        strings.add("%starting_title%");
        strings.add("%starting_text%");
        strings.add("%news_value%");
        strings.add("%news_value%");
        strings.add("%news_value%");
        strings.add("%news_upcoming%");
        strings.add("%news_instance_creator%");
        strings.add("%news_instance_importer%");
        strings.add("%news_probably_more%");
        strings.add("%news_pb7%");
        strings.add("%news_changelog_pb7%");
        strings.add("%news_pb6%");
        strings.add("%news_changelog_pb6%");
        strings.add("%news_pb5%");
        strings.add("%news_changelog_pb5%");
        strings.add("%news_pb4%");
        strings.add("%news_changelog_pb4%");
        strings.add("%news_pb3%");
        strings.add("%news_changelog_pb3%");
        strings.add("%news_pb2%");
        strings.add("%news_changelog_pb2%");
        strings.add("%news_pb1%");
        strings.add("%news_changelog_pb1%");
        strings.add("%start%");
        strings.add("%close%");
        strings.add("%zyneonplus_select_version%");
        strings.add("%zyneonplus_change_version%");
        strings.add("%zyneonplus_slogan%");
        strings.add("%zyneonplus_description%");
        strings.add("%zyneonplus_versiontext%");
        strings.add("%play%");
        strings.add("%worlds%");
        strings.add("%description%");
        for(String path:strings) {
            String string = Main.language.getString(path.replace("-", ".").replace("_", ".").replace("%",""));
            Main.getLogger().debug("(syncLanguage) path: "+path+", converted: "+path.replace("-", ".").replace("_", "."));
            if (string != null) {
                frame.getBrowser().executeJavaScript("javascript:syncLanguage('" + path + "','" + string + "')", "https://danieldieeins.github.io/ZyneonApplicationContent/h/account.html", 5);
            }
            Main.getLogger().debug("(syncLanguage) value: "+string);
        }
    }

    public enum InstanceAction {
        RUN,
        OPEN_FOLDER,
        SHOW_SCREENSHOTS,
        SHOW_RESOURCEPACKS,
        SHOW_SHADERS,
        SHOW_WORLDS,
        SETTINGS_MEMORY
    }
}