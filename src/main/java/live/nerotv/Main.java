package live.nerotv;

import live.nerotv.zyneon.app.backend.utils.Config;
import live.nerotv.zyneon.app.frontend.WebViewApp;
import java.io.File;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {

    public static WebViewApp mainWindow;
    private static String[] arguments;
    private static String path;
    public static Config config;

    public static void main(String[] args) {
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
        config = new Config(new File(getDirectoryPath()+"config.json"));
        mainWindow = new WebViewApp();
        mainWindow.start(args);
        arguments = args;
    }

    public static String[] getArguments() {
        return arguments;
    }

    public static String getDirectoryPath() {
        return URLDecoder.decode(path,StandardCharsets.UTF_8);
    }
}