package com.zyneonstudios.application;

import com.formdev.flatlaf.FlatDarkLaf;
import com.zyneonstudios.Main;
import com.zyneonstudios.application.auth.MicrosoftAuth;
import com.zyneonstudios.application.installer.java.OperatingSystem;
import com.zyneonstudios.application.integrations.index.zyndex.ZyndexIntegration;
import com.zyneonstudios.application.integrations.index.zyndex.instance.ReadableInstance;
import com.zyneonstudios.application.utils.backend.MinecraftVersion;
import com.zyneonstudios.application.utils.backend.Runner;
import com.zyneonstudios.application.utils.frame.web.CustomWebFrame;
import com.zyneonstudios.application.utils.frame.web.ZyneonWebFrame;
import live.nerotv.shademebaby.ShadeMeBaby;
import live.nerotv.shademebaby.file.Config;
import me.friwi.jcefmaven.CefInitializationException;
import me.friwi.jcefmaven.UnsupportedPlatformException;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class Application {

    public static Config config;
    private static ZyneonWebFrame frame;
    public static String startTab = "start";
    public static int memory;
    public static String instancePath;
    public static MicrosoftAuth auth;
    public static Config instances;
    public static String version;
    public static String theme;
    public static boolean logOutput;
    public static boolean thirdPartyWarn;
    public static String lastInstance;
    public static ArrayList<String> args;

    public static String ui;
    private final Runner runner;
    public static ArrayList<String> running = new ArrayList<>();

    public Application(ArrayList<String> arguments) {
        args = arguments;
        runner = new Runner(this);
        if(args.size()>1) {
            Application.ui = arguments.get(0);
            version = arguments.get(1);
        } else {
            throw new RuntimeException("Missing arguments");
        }
    }

    public Runner getRunner() {
        return runner;
    }

    private void init() {
        initConfig();
        try {
            FlatDarkLaf.setup();
            UIManager.setLookAndFeel(new FlatDarkLaf());
        } catch (Exception ignore) {}
    }

    private void initConfig() {
        config = new Config(new File(Main.getDirectoryPath() + "config.json"));
        config.checkEntry("settings.starttab","start");
        config.checkEntry("settings.language","auto");
        config.checkEntry("settings.logOutput",false);
        config.checkEntry("settings.memory.default", 1024);
        config.checkEntry("settings.logger.debug", false);
        config.checkEntry("settings.appearance.theme","default.dark");
        config.checkEntry("settings.lastInstance","zyneon::overview");
        config.checkEntry("settings.warnings.thirdParty",true);

        thirdPartyWarn = config.getBool("settings.warnings.thirdParty");
        logOutput = config.getBool("settings.logOutput");
        theme = config.getString("settings.appearance.theme");
        memory = config.getInteger("settings.memory.default");
        startTab = config.getString("settings.starttab");
        if(config.getString("settings.lastInstance").equalsIgnoreCase("zyneon::overview")) {
            lastInstance = null;
        } else {
            lastInstance = config.getString("settings.lastInstance");
        }
        if(!Main.getLogger().isDebugEnabled()) {
            Main.getLogger().setDebugEnabled(config.getBool("settings.logger.debug"));
            ShadeMeBaby.getLogger().setDebugEnabled(Main.getLogger().isDebugEnabled());
        }
    }

    public void start(boolean online) {
        Application.online = online;
        init();
        try {
            CompletableFuture.runAsync(Application::login);
            Main.getLogger().log("[APP] Syncing available Minecraft versions...");
            MinecraftVersion.syncVersions();
            try {
                Main.getLogger().log("[APP] Trying to sync installed instances...");
                loadInstances();
            } catch (Exception e) {
                Main.getLogger().debug("[APP] Couldn't sync installed instances: "+e.getMessage());
            }
            Main.getLogger().log("[APP] Setting up frame and webview...");
            checkURL();
            Main.getLogger().log("[APP] Styling webview frame...");
            frame.setTitlebar("Zyneon Application", Color.black, Color.white);
            frame.setVisible(true);
            frame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    System.exit(0);
                }
            });
            try {
                frame.setIconImage(ImageIO.read(Objects.requireNonNull(getClass().getResource("/logo.png"))).getScaledInstance(32, 32, Image.SCALE_SMOOTH));
            } catch (IOException ignore) {}
            Main.getLogger().log("[APP] Showing webview frame and hiding splash icon...");
            Main.splash.setVisible(false);
        } catch (UnsupportedPlatformException | CefInitializationException | IOException | InterruptedException e) {
            Main.getLogger().error("[APP] FATAL: Couldn't start Zyneon Application: "+e.getMessage());
            throw new RuntimeException(e);
        }
        System.gc();
        Main.getLogger().log("[APP] Zyneon Application successfully started!");
    }

    public static void loadInstances() {
        File file = new File(Main.getDirectoryPath() + "libs/zyneon/instances.json");
        Main.getLogger().debug("[APP] Created instance json path: " + file.getParentFile().mkdirs());
        if (file.exists()) {
            Main.getLogger().debug("[APP] Deleted old instance json: " + file.delete());
        }
        instances = new Config(file);
        List<Map<String, Object>> instanceList = new ArrayList<>();

        File officialPath = new File(getInstancePath() + "instances/official/");
        Main.getLogger().debug("[APP] Created official instance path: " + officialPath.mkdirs());
        File[] officialInstances = officialPath.listFiles();
        if (officialInstances != null) {
            for (File instance : officialInstances) {
                if (instance.isDirectory()) {
                    if (!instance.getName().equals("zyneonplus")) {
                        saveInstance(instanceList, instance);
                    } else {
                        File[] zyneonInstances = instance.listFiles();
                        if (zyneonInstances != null) {
                            for (File zynstance : zyneonInstances) {
                                if (zynstance.isDirectory()) {
                                    saveInstance(instanceList, zynstance);
                                }
                            }
                        }
                    }
                }
            }
        }

        File unofficialPath = new File(getInstancePath() + "instances/");
        Main.getLogger().debug("[APP] Created unofficial instance path: " + unofficialPath.mkdirs());
        File[] unofficialInstances = unofficialPath.listFiles();
        if (unofficialInstances != null) {
            for (File instance : unofficialInstances) {
                if (instance.isDirectory()) {
                    if (!instance.getName().equalsIgnoreCase("official")) {
                        saveInstance(instanceList, instance);
                    }
                }
            }
        }

        instances.set("instances", instanceList);
    }

    private static boolean saveInstance(List<Map<String, Object>> instanceList, File local) {
        try {
            File instanceFile = new File(local + "/zyneonInstance.json");
            if (instanceFile.exists()) {
                Map<String, Object> instance_ = new HashMap<>();
                ReadableInstance instance = new ReadableInstance(instanceFile);
                if (instance.getSchemeVersion() == null) {
                    instance = new ReadableInstance(ZyndexIntegration.convert(instanceFile));
                } else if (instance.getSchemeVersion().contains("2024.2")) {
                    instance = new ReadableInstance(ZyndexIntegration.convert(instanceFile));
                }
                if (instance.getIconUrl() != null) {
                    instance_.put("icon", instance.getIconUrl());
                }
                String modloader = instance.getModloader();
                if (modloader.equalsIgnoreCase("forge")) {
                    modloader = "Forge " + instance.getForgeVersion();
                } else if (modloader.equalsIgnoreCase("fabric")) {
                    modloader = "Fabric " + instance.getFabricVersion();
                }
                instance_.put("id", instance.getId());
                instance_.put("name", instance.getName());
                instance_.put("version", instance.getVersion());
                instance_.put("minecraft", instance.getMinecraftVersion());
                instance_.put("modloader", modloader);
                instanceList.add(instance_);
            }
            return true;
        } catch (Exception e) {
            Main.getLogger().err("[APPLICATION] Couldn't add instance to list: "+e.getMessage());
            return false;
        }
    }

    public static void login() {
        try {
            if (auth != null) {
                auth.destroy();
                auth = null;
                System.gc();
            }
            auth = new MicrosoftAuth();
        } catch (Exception e) {
            Main.getLogger().error("[APP] Couldn't login: " + e.getMessage());
        }
    }

    public static String getStartURL() {
        String url = "";
        if (startTab.equalsIgnoreCase("instances")) {
            url = getInstancesURL();
        } else {
            url = getNewsURL();
        }
        if(url.contains("?")) {
            url=url+"&theme="+theme;
        } else {
            url=url+"?theme="+theme;
        }
        return url;
    }

    public static boolean online = false;
    public static String getOnlineStartURL() {
        online = !online;
        return getStartURL();
    }

    public static String getNewsURL() {
        if(online) {
            return "https://danieldieeins.github.io/Zyneon-Application/content/start.html";
        } else {
            return "file://" + Main.getDirectoryPath() + "libs/zyneon/" + Application.ui + "/" + "start.html";
        }
    }

    public static String getInstancesURL() {
        if(online) {
            return "https://danieldieeins.github.io/Zyneon-Application/content/instances.html";
        } else {
            return "file://" + Main.getDirectoryPath() + "libs/zyneon/" + Application.ui + "/" + "instances.html";
        }
    }

    public static String getSettingsURL() {
        if(online) {
            return "https://danieldieeins.github.io/Zyneon-Application/content/settings.html";
        } else {
            return "file://" + Main.getDirectoryPath() + "libs/zyneon/" + Application.ui + "/" + "settings.html";
        }
    }

    private void checkURL() throws IOException, UnsupportedPlatformException, CefInitializationException, InterruptedException {
        if(Main.operatingSystem==OperatingSystem.Linux) {
            frame = new CustomWebFrame(getStartURL());
        } else {
            frame = new ZyneonWebFrame(getStartURL());
            frame.pack();
        }
        frame.setMinimumSize(new Dimension(1200,500));
        frame.setSize(new Dimension(1200,720));
        frame.setResizable(true);
        frame.setLocationRelativeTo(null);
    }

    public static ZyneonWebFrame getFrame() {
        return frame;
    }

    public static String getInstancePath() {
        if(instancePath==null) {
            config.checkEntry("settings.path.instances","default");
            if(config.getString("settings.path.instances").equals("default")) {
                Application.getFrame().getBrowser().loadURL(Application.getSettingsURL()+"?tab=select");
                throw new RuntimeException("No instance path");
            } else {
                try {
                    String path = config.getString("settings.path.instances");
                    if(!path.toLowerCase().contains("zyneon")) {
                        path = path+"/Zyneon/";
                    }
                    File instanceFolder = new File(URLDecoder.decode(path, StandardCharsets.UTF_8));
                    Main.getLogger().debug("[APP] Instance path created: "+instanceFolder.mkdirs());
                    instancePath = instanceFolder.getAbsolutePath();
                } catch (Exception e) {
                    Main.getLogger().error("[APP] Instance path invalid - Please select a new one! Falling back to default path.");
                    throw new RuntimeException("No instance path");
                }
            }
        }
        return instancePath.replace("\\","/")+"/";
    }
}