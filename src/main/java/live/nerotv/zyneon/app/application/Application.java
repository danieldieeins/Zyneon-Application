package live.nerotv.zyneon.app.application;

import live.nerotv.Main;
import live.nerotv.shademebaby.file.Config;
import live.nerotv.zyneon.app.application.backend.auth.MicrosoftAuth;
import live.nerotv.zyneon.app.application.backend.utils.HTTPServer;
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
import java.util.*;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class Application {

    private static String version;
    private static ZyneonWebFrame frame;
    public static MicrosoftAuth auth;
    public static Config instances;

    public Application(String v) {
        version = v;
    }

    public void start() {
        login();
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            loadInstances();
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                new HTTPServer(1624).run();
            });
        } catch (Exception ignore) {}
        try {
            checkURL();
            auth.isLoggedIn();
            frame.setTitle("Zyneon Application ("+version+")");
            frame.setVisible(true);
            Main.splash.setVisible(false);
            frame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    System.exit(0);
                }
            });
            try {
                frame.setIconImage(ImageIO.read(Objects.requireNonNull(getClass().getResource("/logo.png"))).getScaledInstance(32,32, Image.SCALE_SMOOTH));
            } catch (IOException ignore) {}
        } catch (UnsupportedPlatformException | CefInitializationException | IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static void loadInstances() {
        File file = new File(Main.getDirectoryPath()+"libs/zyneon/"+Main.v+"/assets/json/instances.json");
        Main.getLogger().debug("Created instance json path: "+file.getParentFile().mkdirs());
        if(file.exists()) {
            Main.getLogger().debug("Deleted old instance json: "+file.delete());
        }
        instances = new Config(file);
        List<Map<String, Object>> instanceList = new ArrayList<>();

        File officialPath = new File(Main.getInstancePath()+"instances/official/");
        Main.getLogger().debug("Created official instance path: "+officialPath.mkdirs());
        File[] officialInstances = officialPath.listFiles();
        if(officialInstances!=null) {
            for(File instance:officialInstances) {
                if(instance.isDirectory()) {
                    if (instance.getName().equals("zyneonplus")) {
                        File[] zyneonInstances = instance.listFiles();
                        if (zyneonInstances != null) {
                            for (File zynstance : zyneonInstances) {
                                if (zynstance.isDirectory()) {
                                    saveInstance(instanceList, zynstance);
                                }
                            }
                        }
                    } else {
                        saveInstance(instanceList, instance);
                    }
                }
            }
        }

        File unofficialPath = new File(Main.getInstancePath()+"instances/");
        Main.getLogger().debug("Created unofficial instance path: "+unofficialPath.mkdirs());
        File[] unofficialInstances = unofficialPath.listFiles();
        if(unofficialInstances!=null) {
            for(File instance:unofficialInstances) {
                if(instance.isDirectory()) {
                    if(!instance.getName().equalsIgnoreCase("official")) {
                        saveInstance(instanceList, instance);
                    }
                }
            }
        }

        instances.set("instances",instanceList);
    }

    private static void saveInstance(List<Map<String, Object>> instanceList, File instance) {
        File instanceFile = new File(instance+"/zyneonInstance.json");
        if(instanceFile.exists()) {
            Map<String, Object> instance_ = new HashMap<>();
            Config instanceJson = new Config(instanceFile);
            String id = instanceJson.getString("modpack.id");
            String name = instanceJson.getString("modpack.name");
            String version = instanceJson.getString("modpack.version");
            String minecraft = instanceJson.getString("modpack.minecraft");
            String modloader = "Vanilla";
            if(instanceJson.getString("modpack.forge.version")!=null) {
                modloader = "Forge "+instanceJson.getString("modpack.forge.version");
            } else if(instanceJson.getString("modpack.fabric")!=null) {
                modloader = "Fabric "+instanceJson.getString("modpack.fabric");
            }
            instance_.put("id",id);
            instance_.put("name",name);
            instance_.put("version",version);
            instance_.put("minecraft",minecraft);
            instance_.put("modloader",modloader);
            instanceList.add(instance_);
        }
    }

    public static void login() {
        SwingUtilities.invokeLater(() -> {
            auth = new MicrosoftAuth();
            auth.setSaveFilePath(URLDecoder.decode(Main.getDirectoryPath() + "libs/opapi/arun.json", StandardCharsets.UTF_8));
            try {
                KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
                keyGenerator.init(256);
                byte[] key = keyGenerator.generateKey().getEncoded();
                String key_ = new String(Base64.getEncoder().encode(key));
                Config saver = new Config(auth.getSaveFile());
                if (saver.get("op.k") == null) {
                    saver.set("op.k", key_);
                } else {
                    key_ = (String) saver.get("op.k");
                    key = Base64.getDecoder().decode(key_);
                }
                auth.setKey(key);
                auth.isLoggedIn();
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private void checkURL() throws IOException, UnsupportedPlatformException, CefInitializationException, InterruptedException {
        String start = "index.html";
        if (Main.starttab.equalsIgnoreCase("instances")) {
            start = "index.html?tab=instances.html";
        }
        String home = "file://" + Main.getDirectoryPath() + "libs/zyneon/" + Main.v + "/" + start;
        frame = new ZyneonWebFrame(home);
    }

    public static String getVersion() {
        return version;
    }

    public static ZyneonWebFrame getFrame() {
        return frame;
    }
}