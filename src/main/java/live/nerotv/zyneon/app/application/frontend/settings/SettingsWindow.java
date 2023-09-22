package live.nerotv.zyneon.app.application.frontend.settings;

import live.nerotv.shademebaby.file.Config;
import live.nerotv.shademebaby.frame.NFrame;

import java.awt.*;

public class SettingsWindow extends NFrame {

    public SettingsWindow(Config modpackFile) {
        Dimension size = new Dimension(640,480);
        setSize(size); setMinimumSize(size); setMaximumSize(size);
    }

}