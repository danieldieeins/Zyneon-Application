package live.nerotv.zyneon.app.application.backend.utils.backend;

import live.nerotv.zyneon.app.application.Application;

public class AuthResolver {

    public AuthResolver() {}

    public void preAuth() {

    }

    public void postAuth() {
        Application.getFrame().getBrowser().loadURL(Application.getSettingsURL()+"&tab=profile");
    }
}
