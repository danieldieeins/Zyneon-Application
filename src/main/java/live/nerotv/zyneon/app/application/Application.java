package live.nerotv.zyneon.app.application;

import live.nerotv.Main;
import live.nerotv.shademebaby.file.Config;
import live.nerotv.zyneon.app.application.backend.auth.MicrosoftAuth;
import live.nerotv.zyneon.app.application.backend.utils.frame.ZyneonWebFrame;
import me.friwi.jcefmaven.CefInitializationException;
import me.friwi.jcefmaven.UnsupportedPlatformException;
import javax.crypto.KeyGenerator;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Objects;

public class Application {

    private static String version;
    private static ZyneonWebFrame frame;
    public static MicrosoftAuth auth;

    public Application(String v) {
        version = v;
    }

    public void start() {
        login();
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
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignore) {}
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