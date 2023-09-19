package live.nerotv.zyneon.app.application.frontend;

import live.nerotv.Main;
import live.nerotv.openlauncherapi.auth.SimpleMicrosoftAuth;
import live.nerotv.zyneon.app.application.Application;
import live.nerotv.zyneon.app.application.backend.launcher.FabricLauncher;
import live.nerotv.zyneon.app.application.backend.launcher.ForgeLauncher;
import live.nerotv.zyneon.app.application.backend.modpack.FabricPack;
import live.nerotv.zyneon.app.application.backend.modpack.ForgePack;
import live.nerotv.zyneon.app.application.backend.utils.Config;
import live.nerotv.zyneon.app.application.frontend.settings.MemoryWindow;

import java.awt.*;
import java.io.*;
import java.net.URI;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class BackendConnectorV2 {

    private SimpleMicrosoftAuth auth;
    private ArrayList<String> us;
    private JCefFrame frame;

    private ForgeLauncher forgeLauncher;
    private FabricLauncher fabricLauncher;

    public BackendConnectorV2(SimpleMicrosoftAuth auth, ArrayList<String> us, JCefFrame frame) {
        this.frame = frame;
        this.auth = auth;
        this.us = us;

        forgeLauncher = new ForgeLauncher(auth, frame);
        fabricLauncher = new FabricLauncher(auth, frame);
    }

    public void resolveRequest(String request) {
        if(request.equals("connector.sync")) {
            if(auth.isLoggedIn()) {
                frame.getBrowser().executeJavaScript("javascript:syncAccount('"+auth.getAuthInfos().getUsername()+"')", "https://danieldieeins.github.io/ZyneonApplicationContent/h/account.html", 5);
                frame.getBrowser().executeJavaScript("javascript:syncButton('Abmelden')", "https://danieldieeins.github.io/ZyneonApplicationContent/h/account.html", 5);
            } else {
                frame.getBrowser().executeJavaScript("javascript:syncAccount('Nicht eingeloggt')", "https://danieldieeins.github.io/ZyneonApplicationContent/h/account.html", 5);
                frame.getBrowser().executeJavaScript("javascript:syncButton('Anmelden')", "https://danieldieeins.github.io/ZyneonApplicationContent/h/account.html", 5);
            }
        } else if (request.equals("button.accessmode")) {
            if (auth.isLoggedIn()) {
                if (us.contains(auth.getAuthInfos().getUuid())) {
                    frame.getBrowser().loadURL("https://danieldieeins.github.io/ZyneonApplicationContent/h/access.html");
                }
            }
        } else if (request.equals("button.settings.argria2")) {
            new MemoryWindow(Main.config, "Argria II RAM Einstellungen ändern", "argria2");
        } else if (request.equals("button.settings.zyneonplus1201")) {
            new MemoryWindow(Main.config, "Zyneon+ 1.20 RAM Einstellungen ändern", "zyneonplus1201");
        } else if (request.equals("button.settings.zyneonplus1194")) {
            new MemoryWindow(Main.config, "Zyneon+ 1.19 RAM Einstellungen ändern", "zyneonplus1194");
        } else if (request.equals("button.settings.zyneonplus1182")) {
            new MemoryWindow(Main.config, "Zyneon+ 1.18 RAM Einstellungen ändern", "zyneonplus1182");
        } else if (request.equals("button.settings.zyneonplus1171")) {
            new MemoryWindow(Main.config, "Zyneon+ 1.17 RAM Einstellungen ändern", "zyneonplus1171");
        } else if (request.equals("button.settings.zyneonplus1165")) {
            new MemoryWindow(Main.config, "Zyneon+ 1.16 RAM Einstellungen ändern", "zyneonplus1165");
        } else if (request.equals("button.settings.memory")) {
            new MemoryWindow(Main.config, "Standard RAM Einstellungen ändern", null);
        } else if (request.equals("button.normalmode")) {
            frame.getBrowser().loadURL("https://danieldieeins.github.io/ZyneonApplicationContent/h/index.html");
        } else if (request.equals("button.account")) {
            if(auth.isLoggedIn()) {
                resolveRequest("button.logout");
                return;
            }
            auth.startAsyncWebview();
            resolveRequest("connector.sync");
        } else if (request.contains("button.logout")) {
            System.out.println("0");
            if (auth.isLoggedIn()) {
                System.out.println("1");
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
        } else if (request.contains("button.labynet")) {
            if (auth.isLoggedIn()) {
                String url = "https://laby.net/@" + auth.getAuthInfos().getUsername();
                if (Desktop.isDesktopSupported()) {
                    Desktop desktop = Desktop.getDesktop();
                    if (desktop.isSupported(Desktop.Action.BROWSE)) {
                        try {
                            desktop.browse(new URI(url));
                        } catch (Exception ignore) {
                        }
                    }
                }
            } else {
                frame.getBrowser().executeJavaScript("javascript:OpenModal('notLoggedIn')", "https://danieldieeins.github.io/ZyneonApplicationContent/h/account.html", 5);
            }
        } else if (request.contains("button.start.")) {
            if(request.contains("zyverse")) {
                String id = request.replace("button.start.", "");
                startInstance(id);
                return;
            }
            if (auth.isLoggedIn()) {
                String id = request.replace("button.start.", "");
                startInstance(id);
            } else {
                frame.getBrowser().executeJavaScript("javascript:OpenModal('notLoggedIn')", "https://danieldieeins.github.io/ZyneonApplicationContent/h/account.html", 5);
            }
        }
    }

    public void startInstance(String id) {
        int ram = 2048;
        if (Main.config.get("settings.memory.default") != null) {
            ram = (int) Main.config.get("settings.memory.default");
        }
        if (id.contains("zyverse")) {
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
        } else if (id.contains("argria2")) {
            forgeLauncher.launch(new ForgePack("https://raw.githubusercontent.com/danieldieeins/ZyneonApplicationContent/main/m/argria2.json"), ram);
        } else if (id.contains("zyneonplus")) {
            startZyneonPlus(id.replace("button", "").replace("zyneonplus", "").replace("start", "").replace(".", ""), ram);
        } else if (id.contains("ukzplite")) {
            forgeLauncher.launch(new ForgePack("https://a.nerotv.live/zyneon/application/modpack/ukzplite.json"), ram);
        } else if (id.contains("ukzp")) {
            forgeLauncher.launch(new ForgePack("https://a.nerotv.live/zyneon/application/modpack/ukzp.json"), ram);
        } else if (id.contains("primal3de")) {
            forgeLauncher.launch(new ForgePack("https://a.nerotv.live/zyneon/application/modpack/primal3de.json"), ram);
        } else if (id.contains("primal")) {
            forgeLauncher.launch(new ForgePack("https://a.nerotv.live/zyneon/application/modpack/primal.json"), ram);
        } else if (id.contains("projectz2")) {
            forgeLauncher.launch(new ForgePack("https://a.nerotv.live/zyneon/application/modpack/projectz2.json"), ram);
        } else if (id.contains("projectz3")) {
            forgeLauncher.launch(new ForgePack("https://a.nerotv.live/zyneon/application/modpack/projectz3.json"), ram);
        } else if (id.contains("zyneontools")) {
            forgeLauncher.launch(new ForgePack("https://a.nerotv.live/zyneon/application/modpack/zyneontools.json"), ram);
        }
    }

    public void startZyneonPlus(String versionID, int ram) {
        if (versionID.equalsIgnoreCase("1165")) {
            fabricLauncher.launch(new FabricPack("https://raw.githubusercontent.com/danieldieeins/ZyneonApplicationContent/main/m/zyneonplus/1.16.5.json"), ram);
        } else if (versionID.equalsIgnoreCase("1171")) {
            fabricLauncher.launch(new FabricPack("https://raw.githubusercontent.com/danieldieeins/ZyneonApplicationContent/main/m/zyneonplus/1.17.1.json"), ram);
        } else if (versionID.equalsIgnoreCase("1182")) {
            fabricLauncher.launch(new FabricPack("https://raw.githubusercontent.com/danieldieeins/ZyneonApplicationContent/main/m/zyneonplus/1.18.2.json"), ram);
        } else if (versionID.equalsIgnoreCase("1194")) {
            fabricLauncher.launch(new FabricPack("https://raw.githubusercontent.com/danieldieeins/ZyneonApplicationContent/main/m/zyneonplus/1.19.4.json"), ram);
        } else if (versionID.equalsIgnoreCase("1201")) {
            fabricLauncher.launch(new FabricPack("https://raw.githubusercontent.com/danieldieeins/ZyneonApplicationContent/main/m/zyneonplus/1.20.1.json"), ram);
        }
    }
}