package live.nerotv.zyneon.launcher;

import live.nerotv.zyneon.launcher.frontend.ZyneonFront;

import java.io.IOException;
import java.net.URISyntaxException;

public class ZyneonLauncher {

    private ZyneonFront frontend;

    public ZyneonLauncher() {
        try {
            frontend = new ZyneonFront();
        } catch (URISyntaxException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void start() {
        frontend.open();
    }

    public void stop() {
        frontend.close();
    }

}