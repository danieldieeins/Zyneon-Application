package live.nerotv.zyneon.app.application.backend.utils.backend.connector;

import live.nerotv.openlauncherapi.auth.AuthResolver;
import live.nerotv.zyneon.app.application.backend.utils.frame.ZyneonWebFrame;

public class ZyneonAuthResolver implements AuthResolver {

    private final ZyneonWebFrame frame;

    public ZyneonAuthResolver(ZyneonWebFrame frame) {
        this.frame = frame;
    }

    @Override
    public void preAuth() {
        AuthResolver.super.preAuth();
    }

    @Override
    public void postAuth(String name, String suid) {
        frame.getBrowser().reload();
        frame.getBrowser().executeJavaScript("javascript:syncAccount('"+name+"')", "https://danieldieeins.github.io/ZyneonApplicationContent/h/account.html", 5);
        frame.getBrowser().executeJavaScript("javascript:syncButton('Abmelden')", "https://danieldieeins.github.io/ZyneonApplicationContent/h/account.html", 5);
    }
}
