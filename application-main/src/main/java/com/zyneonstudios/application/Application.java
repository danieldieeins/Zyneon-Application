package com.zyneonstudios.application;

import com.formdev.flatlaf.FlatDarkLaf;
import com.zyneonstudios.Main;
import com.zyneonstudios.application.backend.auth.MicrosoftAuth;
import com.zyneonstudios.application.backend.utils.backend.MinecraftVersion;
import com.zyneonstudios.application.backend.utils.frame.web.UnixWebFrame;
import com.zyneonstudios.application.backend.utils.frame.web.ZyneonWebFrame;
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

    public Application(String ver) {
        version = ver;
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
        config.checkEntry("settings.appearance.theme","dark");

        logOutput = config.getBool("settings.logOutput");
        theme = config.getString("settings.appearance.theme");
        memory = config.getInteger("settings.memory.default");
        startTab = config.getString("settings.starttab");
        if(!Main.getLogger().isDebugEnabled()) {
            Main.getLogger().setDebugEnabled(config.getBool("settings.logger.debug"));
            ShadeMeBaby.getLogger().setDebugEnabled(Main.getLogger().isDebugEnabled());
        }
    }

    public void start() {
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

    private static void saveInstance(List<Map<String, Object>> instanceList, File instance) {
        File instanceFile = new File(instance + "/zyneonInstance.json");
        if (instanceFile.exists()) {
            Map<String, Object> instance_ = new HashMap<>();
            Config instanceJson = new Config(instanceFile);
            String id = instanceJson.getString("modpack.id");
            String name = instanceJson.getString("modpack.name");
            String version = instanceJson.getString("modpack.version");
            String minecraft = instanceJson.getString("modpack.minecraft");
            if (instanceJson.getString("modpack.icon") != null) {
                instance_.put("icon", instanceJson.getString("modpack.icon"));
            }
            String modloader = "Vanilla";
            if (instanceJson.getString("modpack.forge.version") != null) {
                modloader = "Forge " + instanceJson.getString("modpack.forge.version");
            } else if (instanceJson.getString("modpack.fabric") != null) {
                modloader = "Fabric " + instanceJson.getString("modpack.fabric");
            }
            instance_.put("id", id);
            instance_.put("name", name);
            instance_.put("version", version);
            instance_.put("minecraft", minecraft);
            instance_.put("modloader", modloader);
            instanceList.add(instance_);
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
        if (startTab.equalsIgnoreCase("instances")) {
            return getInstancesURL();
        }
        return getNewsURL();
    }

    public static String getNewsURL() {
        return "file://" + Main.getDirectoryPath() + "libs/zyneon/" + Main.version + "/" + "start.html";
    }

    public static String getInstancesURL() {
        return "file://" + Main.getDirectoryPath() + "libs/zyneon/" + Main.version + "/" + "instances.html";
    }

    public static String getSettingsURL() {
        return "file://" + Main.getDirectoryPath() + "libs/zyneon/" + Main.version + "/" + "settings.html";
    }

    private void checkURL() throws IOException, UnsupportedPlatformException, CefInitializationException, InterruptedException {
        if(Main.os.equalsIgnoreCase("windows")) {
            frame = new ZyneonWebFrame(getStartURL());
            frame.pack();
        } else {
            frame = new UnixWebFrame(getStartURL());
        }
        frame.setMinimumSize(new Dimension(960,500));
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