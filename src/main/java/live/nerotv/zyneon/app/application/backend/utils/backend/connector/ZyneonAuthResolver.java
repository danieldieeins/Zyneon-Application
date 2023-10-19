package live.nerotv.zyneon.app.application.backend.utils.backend.connector;

import live.nerotv.zyneon.app.application.Application;

public class ZyneonAuthResolver {

    public ZyneonAuthResolver() {

    }

    public void preAuth() {

    }

    public void postAuth(String name, String suid) {
        Application.getFrame().getBrowser().reload();
    }
}
