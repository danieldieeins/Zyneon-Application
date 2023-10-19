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
        // Erstelle ein neues Fenster
        JFrame frame = new JFrame("Minecraft Login");
        frame.setSize(600, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        // Öffne den Web-Browser mit der URL für die Minecraft-Anmeldung
        try {
            Desktop.getDesktop().browse(new URI("https://www.minecraft.net/de-de/redeem"));
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }

        // Frage den Benutzer nach dem Code, der in Minecraft angezeigt wird
        String code = JOptionPane.showInputDialog(frame, "Gib den Code ein, der in Minecraft angezeigt wird:");

        // Schicke den Code an die Java-App zurück
        // Hier musst du deine eigene Logik implementieren, um die Auth Tokens zu erhalten und zu verwenden
        System.out.println("Der eingegebene Code ist: " + code);
    }
}