package live.nerotv.zyneon.app.application.backend.auth;

import fr.theshark34.openlauncherlib.minecraft.AuthInfos;
import live.nerotv.Main;
import live.nerotv.shademebaby.file.Config;

import javax.crypto.KeyGenerator;
import java.io.File;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class MicrosoftAuth {

    private Authenticator authenticator;

    public MicrosoftAuth() {
        authenticator = new Authenticator();
        try {
            new File(URLDecoder.decode(Main.getDirectoryPath() + "libs/opapi/arun.json", StandardCharsets.UTF_8)).delete();
        } catch (Exception ignore) {}
        authenticator.setSaveFilePath(URLDecoder.decode(Main.getDirectoryPath() + "libs/opapi/arnu.json", StandardCharsets.UTF_8));
        KeyGenerator keyGenerator;
        try {
            keyGenerator = KeyGenerator.getInstance("AES");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        keyGenerator.init(256);
        byte[] key = keyGenerator.generateKey().getEncoded();
        String key_ = new String(Base64.getEncoder().encode(key));
        Config saver = new Config(authenticator.getSaveFile());
        if (saver.get("op.k") == null) {
            saver.set("op.k", key_);
        } else {
            key_ = (String) saver.get("op.k");
            key = Base64.getDecoder().decode(key_);
        }
        authenticator.setKey(key);
        authenticator.isLoggedIn();
    }

    public AuthInfos getAuthInfos() {
        return authenticator.getAuthInfos();
    }

    public boolean isLoggedIn() {
        return authenticator.isLoggedIn();
    }

    public void login() {
        authenticator.login();
    }

    public void setSaveFilePath(String path) {
        authenticator.setSaveFilePath(path);
    }

    public File getSaveFile() {
        return authenticator.getSaveFile();
    }

    public void setKey(byte[] key) {
        authenticator.setKey(key);
    }

    public void destroy() {
        authenticator.destroy();
        authenticator = null;
        System.gc();
    }
}