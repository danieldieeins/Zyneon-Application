package live.nerotv.zyneon.app.application.backend.auth;

import fr327.theshark34.openlauncherlib.minecraft.AuthInfos;

import java.io.File;

@Deprecated
public class MicrosoftAuth {

    private final SimpleMicrosoftAuth sa;

    public MicrosoftAuth() {
        sa = new SimpleMicrosoftAuth();
    }

    public AuthInfos getAuthInfos() {
        return sa.getAuthInfos();
    }

    public boolean isLoggedIn() {
        return sa.isLoggedIn();
    }

    public void startAsyncWebview() {
        sa.startAsyncWebview();
    }

    public void setSaveFilePath(String path) {
        sa.setSaveFilePath(path);
    }

    public File getSaveFile() {
        return sa.getSaveFile();
    }

    public void setKey(byte[] key) {
        sa.setKey(key);
    }
}