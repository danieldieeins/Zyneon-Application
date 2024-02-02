package live.nerotv.zyneon.app.application.backend.auth;

import fr.theshark34.openlauncherlib.minecraft.AuthInfos;

import java.io.File;

public class MicrosoftAuth {

    private final Authenticator authenticator;

    public MicrosoftAuth() {
        authenticator = new Authenticator();
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
}