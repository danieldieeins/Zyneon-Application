package live.nerotv.zyneon.app.application.backend.utils.backend.connector;

import live.nerotv.Main;
import live.nerotv.openlauncherapi.auth.SimpleMicrosoftAuth;
import live.nerotv.zyneon.app.application.Application;
import live.nerotv.zyneon.app.application.backend.instance.FabricInstance;
import live.nerotv.zyneon.app.application.backend.instance.ForgeInstance;
import live.nerotv.zyneon.app.application.backend.instance.VanillaInstance;
import live.nerotv.zyneon.app.application.backend.launcher.FabricLauncher;
import live.nerotv.zyneon.app.application.backend.launcher.ForgeLauncher;
import live.nerotv.zyneon.app.application.backend.launcher.VanillaLauncher;
import live.nerotv.zyneon.app.application.backend.utils.file.Config;
import live.nerotv.zyneon.app.application.backend.utils.file.FileUtils;
import live.nerotv.zyneon.app.application.backend.utils.frame.ZyneonWebFrame;
import live.nerotv.zyneon.app.application.frontend.settings.MemoryWindow;

import java.awt.*;
import java.io.*;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

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
        } else if(request.contains("button.resourcepacks.")) {
            resolveInstanceRequest(InstanceAction.SHOW_RESOURCEPACKS,request.replace("button.resourcepacks.",""));
        } else if(request.contains("button.shaders.")) {
            resolveInstanceRequest(InstanceAction.SHOW_SHADERS,request.replace("button.shaders.",""));
        } else if(request.contains("button.worlds.")) {
            resolveInstanceRequest(InstanceAction.SHOW_WORLDS, request.replace("button.worlds.", ""));
        } else if(request.contains("button.settings.")) {
            resolveInstanceRequest(InstanceAction.SETTINGS_MEMORY, request.replace("button.settings.", ""));
        } else if(request.contains("button.account")) {
            if(auth.isLoggedIn()) {
                resolveRequest("button.logout");
                return;
            }
            auth.startAsyncWebview();
            resolveRequest("connector.sync");
        } else if(request.contains("button.logout")) {
            if (auth.isLoggedIn()) {
                auth.getSaveFile().delete();
                auth = new SimpleMicrosoftAuth();
                Application.login();
                if (auth.isLoggedIn()) {
                    frame.setTitle("Zyneon Application (" + Application.getVersion() + ", " + auth.getAuthInfos().getUsername() + ")");
                } else {
                    frame.setTitle("Zyneon Application (" + Application.getVersion() + ")");
                }
            } else {
                frame.getBrowser().executeJavaScript("javascript:OpenModal('notLoggedIn')", "https://danieldieeins.github.io/ZyneonApplicationContent/h/account.html", 5);
            }
            resolveRequest("connector.sync");
        } else if(request.contains("connector.sync")) {
            if(auth.isLoggedIn()) {
                frame.getBrowser().executeJavaScript("javascript:syncAccount('"+auth.getAuthInfos().getUsername()+"')", "https://danieldieeins.github.io/ZyneonApplicationContent/h/account.html", 5);
                frame.getBrowser().executeJavaScript("javascript:syncButton('Abmelden')", "https://danieldieeins.github.io/ZyneonApplicationContent/h/account.html", 5);
            } else {
                frame.getBrowser().executeJavaScript("javascript:syncAccount('Nicht eingeloggt')", "https://danieldieeins.github.io/ZyneonApplicationContent/h/account.html", 5);
                frame.getBrowser().executeJavaScript("javascript:syncButton('Anmelden')", "https://danieldieeins.github.io/ZyneonApplicationContent/h/account.html", 5);
            }
        } else {
            System.out.println("REQUEST: "+request);
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
        System.out.println("Downloading latest version...");
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
                    System.out.println(line);
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
        SHOW_RESOURCEPACKS,
        SHOW_SHADERS,
        SHOW_WORLDS,
        SETTINGS_MEMORY
    }
}