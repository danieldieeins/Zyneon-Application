package live.nerotv;

import live.nerotv.zyneon.app.frontend.WebViewApp;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {

    private static WebViewApp mainWindow;
    private static String[] arguments;

    public static void main(String[] args) {
        getDirectoryPath();
        arguments = args;
        mainWindow = new WebViewApp();
        mainWindow.start(args);
    }

    public static WebViewApp getMainWindow() {
        return mainWindow;
    }

    public static String[] getArguments() {
        return arguments;
    }

    public static String getDirectoryPath() {
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
        return folderPath.toString()+"/";
    }
}