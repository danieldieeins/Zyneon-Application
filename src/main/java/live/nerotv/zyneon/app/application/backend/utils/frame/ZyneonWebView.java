package live.nerotv.zyneon.app.application.backend.utils.frame;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.web.WebView;

import javax.swing.*;
import java.awt.*;

@Deprecated
public class ZyneonWebView {

    public static void main(String[] args) {
        new ZyneonWebView().i();
    }

    public void i() {
        JFrame jFrame = new JFrame();
        jFrame.setSize(new Dimension(1280,820));
        jFrame.setMinimumSize(new Dimension(1280,820));
        jFrame.setLocationRelativeTo(null);
        JFXPanel jfxPanel = new JFXPanel();
        jFrame.add(jfxPanel);
        Platform.runLater(() -> {
            WebView webView = new WebView();
            jfxPanel.setScene(new Scene(webView));
            webView.getEngine().load("https://danieldieeins.github.io/ZyneonApplicationContent/h/");
        });
        jFrame.setVisible(true);
    }
}
