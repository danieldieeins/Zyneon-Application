package live.nerotv;

import live.nerotv.zyneon.app.backend.launcher.FabricLauncher;
import live.nerotv.zyneon.app.backend.launcher.ForgeLauncher;
import live.nerotv.zyneon.app.backend.launcher.VanillaLauncher;
import live.nerotv.zyneon.app.backend.login.MicrosoftAuth;
import live.nerotv.zyneon.app.backend.utils.Config;
import live.nerotv.zyneon.app.frontend.JCefFrame;
import me.friwi.jcefmaven.CefInitializationException;
import me.friwi.jcefmaven.UnsupportedPlatformException;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {

    private static String[] arguments;
    private static String path;
    public static Config config;
    public static JCefFrame frame;
    private static FabricLauncher fabricLauncher;
    private static ForgeLauncher forgeLauncher;
    private static VanillaLauncher vanillaLauncher;

    public static void main(String[] args) {
        config = new Config(new File(getDirectoryPath()+"config.json"));
        arguments = args;
        try {
            boolean offline = true;
            if(args.length != 0) {
                if(args[0].equalsIgnoreCase("online")) {
                    offline = false;
                }
            }
            if(offline) {
                frame = new JCefFrame(null);
            } else {
                frame = new JCefFrame("https://a.nerotv.live/zyneon/launcher/html2/index.html");
            }
            if(MicrosoftAuth.isUserSignedIn()) {
                frame.setTitle("Zyneon Application (Alpha 0.1.0, "+MicrosoftAuth.getAuthInfos().getUsername()+")");
            } else {
                frame.setTitle("Zyneon Application (Alpha 0.1.0)");
            }
            frame.setMinimumSize(new Dimension(1280,800));
            frame.open();
        } catch (UnsupportedPlatformException | CefInitializationException | IOException | InterruptedException e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    public static String[] getArguments() {
        return arguments;
    }

    public static String getDirectoryPath() {
        if(path == null) {
            String folderName = "ZyneonLauncher";
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
}