package live.nerotv.zyneon.app.frontend.settings;

import live.nerotv.zyneon.app.backend.utils.Config;
import javax.swing.*;
import java.awt.*;

public class SettingsWindow extends JFrame {

    public SettingsWindow(Config modpackFile) {
        Dimension size = new Dimension(640,480);
        setSize(size); setMinimumSize(size); setMaximumSize(size);
    }

}