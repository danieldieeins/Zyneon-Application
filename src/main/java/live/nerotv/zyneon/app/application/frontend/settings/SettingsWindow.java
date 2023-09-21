package live.nerotv.zyneon.app.application.frontend.settings;

import live.nerotv.zyneon.app.application.backend.utils.file.Config;
import live.nerotv.zyneon.app.application.backend.utils.frame.ZyneonFrame;

import java.awt.*;

public class SettingsWindow extends ZyneonFrame {

    public SettingsWindow(Config modpackFile) {
        Dimension size = new Dimension(640,480);
        setSize(size); setMinimumSize(size); setMaximumSize(size);
    }

}