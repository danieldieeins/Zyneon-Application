package live.nerotv.zyneon.app.application;

import com.formdev.flatlaf.FlatDarkLaf;
import live.nerotv.Main;
import live.nerotv.shademebaby.file.Config;
import live.nerotv.zyneon.app.application.backend.auth.MicrosoftAuth;
import live.nerotv.zyneon.app.application.backend.framework.MinecraftVersion;
import live.nerotv.zyneon.app.application.backend.utils.frame.ZyneonWebFrame;
import me.friwi.jcefmaven.CefInitializationException;
import me.friwi.jcefmaven.UnsupportedPlatformException;

import javax.crypto.KeyGenerator;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.*;

public class Application {

    private static ZyneonWebFrame frame;
    public static MicrosoftAuth auth;
    public static Config instances;
    public static String version;
    public static String theme;

    public Application(String ver) {
        version = ver;
        theme = Main.config.getString("settings.appearance.theme");
    }

    public void start() {
        login();
        try {
            FlatDarkLaf.setup();
            UIManager.setLookAndFeel(new FlatDarkLaf());
            /*UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());*/
            loadInstances();
        } catch (Exception ignore) {
        }
        try {
            MinecraftVersion.syncVersions();
            checkURL();
            auth.isLoggedIn();
            frame.setTitlebar("Zyneon Application", Color.decode("#050113"), Color.white);
            frame.setVisible(true);
            Main.splash.setVisible(false);
            frame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    System.exit(0);
                }
            });
            try {
                frame.setIconImage(ImageIO.read(Objects.requireNonNull(getClass().getResource("/logo.png"))).getScaledInstance(32, 32, Image.SCALE_SMOOTH));
            } catch (IOException ignore) {
            }
        } catch (UnsupportedPlatformException | CefInitializationException | IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static void loadInstances() {
        File file = new File(Main.getDirectoryPath() + "libs/zyneon/instances.json");
        Main.getLogger().debug("Created instance json path: " + file.getParentFile().mkdirs());
        if (file.exists()) {
            Main.getLogger().debug("Deleted old instance json: " + file.delete());
        }
        instances = new Config(file);
        List<Map<String, Object>> instanceList = new ArrayList<>();

        File officialPath = new File(Main.getInstancePath() + "instances/official/");
        Main.getLogger().debug("Created official instance path: " + officialPath.mkdirs());
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

        File unofficialPath = new File(Main.getInstancePath() + "instances/");
        Main.getLogger().debug("Created unofficial instance path: " + unofficialPath.mkdirs());
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
        if (auth != null) {
            auth.destroy();
            auth = null;
            System.gc();
        }
        auth = new MicrosoftAuth();

    }

    public static String getStartURL() {
        if (Main.starttab.equalsIgnoreCase("instances")) {
            return getInstancesURL();
        }
        return getNewsURL();
    }

    public static String getNewsURL() {
        return "file://" + Main.getDirectoryPath() + "libs/zyneon/" + Main.version + "/" + "index.html?theme=" + theme;
    }

    public static String getInstancesURL() {
        return "file://" + Main.getDirectoryPath() + "libs/zyneon/" + Main.version + "/" + "instances.html?theme=" + theme;
    }

    public static String getSettingsURL() {
        return "file://" + Main.getDirectoryPath() + "libs/zyneon/" + Main.version + "/" + "settings.html?theme=" + theme;
    }

    private void checkURL() throws IOException, UnsupportedPlatformException, CefInitializationException, InterruptedException {
        frame = new ZyneonWebFrame(getStartURL());
    }

    public static ZyneonWebFrame getFrame() {
        return frame;
    }
}