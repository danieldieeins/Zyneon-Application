package live.nerotv.zyneon.app.application.backend.auth;

import fr.theshark34.openlauncherlib.minecraft.AuthInfos;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class MicrosoftAuth {

    private SimpleMicrosoftAuth sa;

    public MicrosoftAuth() {
        sa = new SimpleMicrosoftAuth();
    }

    public void setSMA(SimpleMicrosoftAuth sa) {
        this.sa = sa;
    }

    public SimpleMicrosoftAuth getSMA() {
        return sa;
    }

    @Deprecated
    public AuthInfos getAuthInfos() {
        return sa.getAuthInfos();
    }

    @Deprecated
    public boolean isLoggedIn() {
        return sa.isLoggedIn();
    }

    @Deprecated
    public void startAsyncWebview() {
        sa.startAsyncWebview();
    }

    @Deprecated
    public void setSaveFilePath(String path) {
        sa.setSaveFilePath(path);
    }

    @Deprecated
    public File getSaveFile() {
        return sa.getSaveFile();
    }

    @Deprecated
    public void setKey(byte[] key) {
        sa.setKey(key);
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Minecraft Login");
        frame.setSize(600, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        try {
            Desktop.getDesktop().browse(new URI("https://www.minecraft.net/de-de/redeem"));
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }

        String code = JOptionPane.showInputDialog(frame, "Gib den Code ein, der in Minecraft angezeigt wird:");
        System.out.println("Der eingegebene Code ist: " + code);
    }
}