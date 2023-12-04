package live.nerotv.zyneon.app.application.frontend;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class ZyneonSplash extends JWindow {

    public ZyneonSplash() {
        super();
        try {
            setBackground(new Color(0, 0, 0, 0));
            setSize(400, 400);
            setLocationRelativeTo(null);
            JLabel image;
            image = new JLabel(new ImageIcon(ImageIO.read(getClass().getResourceAsStream("/logo.png")).getScaledInstance(getWidth(), getHeight(), Image.SCALE_SMOOTH)));
            getContentPane().add(image);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}