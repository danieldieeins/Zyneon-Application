package live.nerotv;

import live.nerotv.openlauncherapi.auth.SimpleMicrosoftAuth;
import live.nerotv.zyneon.app.backend.launcher.FabricLauncher;
import live.nerotv.zyneon.app.backend.launcher.ForgeLauncher;
import live.nerotv.zyneon.app.backend.launcher.VanillaLauncher;
import live.nerotv.zyneon.app.backend.login.MicrosoftAuth;
import live.nerotv.zyneon.app.backend.modpack.creator.ModpackCreator;
import live.nerotv.zyneon.app.backend.utils.Config;
import live.nerotv.zyneon.app.frontend.JCefFrame;
import me.friwi.jcefmaven.CefInitializationException;
import me.friwi.jcefmaven.UnsupportedPlatformException;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class Main {

    public static SimpleMicrosoftAuth auth;
    private static String[] arguments;
    private static String path;
    public static Config config;
    public static JCefFrame frame;
    private static FabricLauncher fabricLauncher;
    private static ForgeLauncher forgeLauncher;
    private static VanillaLauncher vanillaLauncher;
    private static boolean sendDebug = false;
    private static String version;

    public static void main(String[] args) {
        version = "1.0.0 Beta 7";
        config = new Config(new File(getDirectoryPath()+"config.json"));
        arguments = args;
        if(arguments.length > 0) {
            if(args[0].equalsIgnoreCase("debug")) {
                sendDebug = true;
            } else if(args[0].equalsIgnoreCase("creator")) {
                new ModpackCreator().start();
                return;
            }
        }
        auth = new SimpleMicrosoftAuth();
        MicrosoftAuth.login();
        try {
            checkURL(getURL());
            if(auth.isLoggedIn()) {
                frame.setTitle("Zyneon Application ("+version+", "+auth.getAuthInfos().getUsername()+")");
            } else {
                frame.setTitle("Zyneon Application ("+version+")");
            }
            frame.setMinimumSize(new Dimension(1280,800));
            frame.open();
        } catch (UnsupportedPlatformException | CefInitializationException | IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getVersion() {
        return version;
    }

    private static void checkURL(String urlString) throws IOException, UnsupportedPlatformException, CefInitializationException, InterruptedException {
        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("HEAD");
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                frame = new JCefFrame(urlString);
            } else {
                frame = new JCefFrame(null);
            }
        } catch (UnknownHostException e) {
            frame = new JCefFrame(null);
        }
    }

    public static String[] getArguments() {
        return arguments;
    }

    public static String getDirectoryPath() {
        if(path == null) {
            String folderName = "ZyneonApplication";
            String appData;
            String os = System.getProperty("os.name").toLowerCase();
            if (os.contains("win")) {
                appData = System.getenv("LOCALAPPDATA");
            } else if (os.contains("mac")) {
                appData = System.getProperty("user.home") + "/Library/Application Support";
            } else {
                appData = System.getProperty("user.home") + "/.local/share";
            }
            Path folderPath = Paths.get(appData, folderName);
            try {
                Files.createDirectories(folderPath);
            } catch (Exception e) {
                e.printStackTrace();
            }
            path = folderPath+"/";
        }
        return URLDecoder.decode(path,StandardCharsets.UTF_8);
    }

    public static FabricLauncher getFabricLauncher() {
        if(fabricLauncher==null) {
            fabricLauncher = new FabricLauncher();
        }
        return fabricLauncher;
    }

    public static ForgeLauncher getForgeLauncher() {
        if(forgeLauncher==null) {
            forgeLauncher = new ForgeLauncher();
        }
        return forgeLauncher;
    }

    public static VanillaLauncher getVanillaLauncher() {
        if(vanillaLauncher==null) {
            vanillaLauncher = new VanillaLauncher();
        }
        return vanillaLauncher;
    }

    public static void debug(String message) {
        if(sendDebug) {
            System.out.println("[DEBUG] " + message);
        }
    }

    public static ArrayList<String> us = new ArrayList<>();
    public static String getURL() {
        if(auth.isLoggedIn()) {
            us = new ArrayList<>();
            String u = auth.getAuthInfos().getUuid();
            us.add("6447757f59fe4206ae3fdc68ff2bb6f0");
            us.add("b9e0e4fa69a149fe93a605afe249639d");
            us.add("cd6731637e9d4bf391b3cd65ff147fff");
            if(us.contains(u)) {
                return "https://a.nerotv.live/zyneon/application/html/admin.html";
            }
        }
        return "https://a.nerotv.live/zyneon/application/html/index.html";
    }
}