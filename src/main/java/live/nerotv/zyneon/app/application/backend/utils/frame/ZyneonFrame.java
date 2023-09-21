package live.nerotv.zyneon.app.application.backend.utils.frame;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.util.Objects;

public class ZyneonFrame extends JFrame {

    public void setIcon(String resourcePath) {
        try {
            setIconImage(new ImageIcon(ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream(resourcePath)))).getImage().getScaledInstance(32, 32, Image.SCALE_SMOOTH));
        } catch (Exception e) {
            System.out.println("Error obtaining icon file: " + e.getMessage());
        }
    }
}