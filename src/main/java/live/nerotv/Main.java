package live.nerotv;

import live.nerotv.shademebaby.ShadeMeBaby;
import live.nerotv.shademebaby.file.Config;
import live.nerotv.shademebaby.file.FileUtils;
import live.nerotv.shademebaby.logger.Logger;
import live.nerotv.zyneon.app.application.Application;
import live.nerotv.zyneon.app.application.backend.utils.FileUtil;
import live.nerotv.zyneon.app.application.frontend.ZyneonSplash;

import java.io.File;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {

    private static String path;
    public static String instances;
    public static Config config;
    private static Logger logger;
    public static String version;
    public static String starttab = "start";
    public static ZyneonSplash splash;
    public static String os;
    public static int memory;

    public static void main(String[] args) {
        splash = new ZyneonSplash();
        splash.setVisible(true);
        initConfig();
        starttab = config.getString("settings.starttab");
        logger = new Logger("ZyneonApplication");
        logger.setDebugEnabled(config.getBool("settings.logger.debug"));
        version = "2024.2-beta.2";
        if(!new File(getDirectoryPath()+"libs/zyneon/"+ version +"/index.html").exists()) {
            FileUtil.deleteFolder(new File(getDirectoryPath()+"libs/zyneon/"));
            getLogger().debug("Deleted old UI Files: "+new File(getDirectoryPath()+"libs/zyneon/").mkdirs());
            FileUtils.downloadFile("https://github.com/danieldieeins/ZyneonApplicationContent/raw/main/h/" + version + "/content.zip", getDirectoryPath() + "libs/zyneon/" + version + ".zip");
            FileUtils.unzipFile(getDirectoryPath() + "libs/zyneon/" + version + ".zip", getDirectoryPath() + "libs/zyneon/" + version);
            getLogger().debug("Deleted UI ZIP File: "+new File(getDirectoryPath() + "libs/zyneon/" + version + ".zip").delete());
        }
        ShadeMeBaby.getLogger().setDebugEnabled(config.getBool("settings.logger.debug"));
        getLogger().debug("Deleted old updater json: "+new File(getDirectoryPath()+"updater.json").delete());
        getLogger().debug("Deleted old version json: "+new File(getDirectoryPath()+"version.json").delete());
        FileUtil.deleteFolder(new File(getDirectoryPath()+"temp/"));
        config.checkEntry("settings.appearance.theme","zyneon");
        new Application(version+" ▪ Argrium²").start();
    }

    private static void initConfig() {
        config = new Config(new File(getDirectoryPath() + "config.json"));
        config.checkEntry("settings.starttab","start");
        config.checkEntry("settings.language","auto");
        config.checkEntry("settings.memory.default", 1024);
        memory = config.getInteger("settings.memory.default");
        config.checkEntry("settings.logger.debug", false);
    }

    public static Logger getLogger() {
        return logger;
    }

    public static String getDirectoryPath() {
        if (path == null) {
            String folderName = "Zyneon/Application";
            String appData;
            String os = System.getProperty("os.name").toLowerCase();
            if (os.contains("win")) {
                Main.os = "Windows";
                appData = System.getenv("LOCALAPPDATA");
            } else if (os.contains("mac")) {
                Main.os = "macOS";
                appData = System.getProperty("user.home") + "/Library/Application Support";
            } else {
                Main.os = "Unix";
                appData = System.getProperty("user.home") + "/.local/share";
            }
            Path folderPath = Paths.get(appData, folderName);
            try {
                Files.createDirectories(folderPath);
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage());
            }
            path = folderPath + "/";
        }
        return URLDecoder.decode(path, StandardCharsets.UTF_8);
    }

    public static String getInstancePath() {
        if(instances==null) {
            config.checkEntry("settings.path.instances","default");
            if(config.getString("settings.path.instances").equals("default")) {
                Application.getFrame().getBrowser().loadURL(Application.getSettingsURL()+"&tab=select");
                throw new RuntimeException("No instance path");
            } else {
                try {
                    String path = config.getString("settings.path.instances");
                    if(!path.toLowerCase().contains("zyneon")) {
                        path = path+"/Zyneon/";
                    }
                    File instanceFolder = new File(URLDecoder.decode(path, StandardCharsets.UTF_8));
                    getLogger().debug("Instance path created: "+instanceFolder.mkdirs());
                    instances = instanceFolder.getAbsolutePath();
                } catch (Exception e) {
                    getLogger().error("Instance path invalid - Please select a new one! Falling back to default path.");
                    Application.getFrame().getBrowser().executeJavaScript("changeFrame('settings/select-instance-path.html');", "https://danieldieeins.github.io/ZyneonApplicationContent/h/account.html", 5);
                    throw new RuntimeException("No instance path");
                }
            }
        }
        return instances.replace("\\","/")+"/";
    }
}