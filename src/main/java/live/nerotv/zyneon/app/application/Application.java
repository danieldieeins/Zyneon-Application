package live.nerotv.zyneon.app.application;

import live.nerotv.Main;
import live.nerotv.openlauncherapi.auth.SimpleMicrosoftAuth;
import live.nerotv.zyneon.app.application.backend.utils.Config;
import live.nerotv.zyneon.app.application.frontend.JCefFrame;
import me.friwi.jcefmaven.CefInitializationException;
import me.friwi.jcefmaven.UnsupportedPlatformException;

import javax.crypto.KeyGenerator;
import java.awt.*;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Base64;

public class Application {

    private static String version;
    private static JCefFrame frame;
    private static SimpleMicrosoftAuth auth;
    private final ArrayList<String> us;

    public Application() {
        version = "1.0.0 Beta G19";
        auth = new SimpleMicrosoftAuth();
        us = new ArrayList<>();
        us.add("6447757f59fe4206ae3fdc68ff2bb6f0");
        us.add("b9e0e4fa69a149fe93a605afe249639d");
        us.add("cd6731637e9d4bf391b3cd65ff147fff");
    }
    public void start() {
        login();
        try {
            checkURL();
            if(auth.isLoggedIn()) {
                frame.setTitle("Zyneon Application ("+version+", "+auth.getAuthInfos().getUsername()+")");
            } else {
                frame.setTitle("Zyneon Application ("+version+")");
            }
            frame.setMinimumSize(new Dimension(1280,820));
            frame.setLocationRelativeTo(null);
            frame.setIcon("/icon.png");
            frame.open();
        } catch (UnsupportedPlatformException | CefInitializationException | IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static void login() {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
            keyGenerator.init(256);
            byte[] key = keyGenerator.generateKey().getEncoded();
            String key_ = new String(Base64.getEncoder().encode(key));
            auth.setSaveFilePath(URLDecoder.decode(Main.getDirectoryPath()+"libs/opapi/arun.json", StandardCharsets.UTF_8));
            Config saver = new Config(auth.getSaveFile());
            if(saver.get("op.k")==null) {
                saver.set("op.k",key_);
            } else {
                key_ = (String)saver.get("op.k");
                key = Base64.getDecoder().decode(key_);
            }
            auth.setKey(key);
            auth.isLoggedIn();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private void checkURL() throws IOException, UnsupportedPlatformException, CefInitializationException, InterruptedException {
        try {
            URL url = new URL("https://danieldieeins.github.io/ZyneonApplicationContent/h/index.html");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("HEAD");
            frame = new JCefFrame(auth,us);
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getVersion() {
        return version;
    }

    public static JCefFrame getFrame() {
        return frame;
    }
}