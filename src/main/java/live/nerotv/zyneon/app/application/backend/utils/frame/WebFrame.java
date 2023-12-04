package live.nerotv.zyneon.app.application.backend.utils.frame;

import javafx.application.Platform;
import live.nerotv.shademebaby.ShadeMeBaby;
import me.friwi.jcefmaven.CefAppBuilder;
import me.friwi.jcefmaven.CefInitializationException;
import me.friwi.jcefmaven.MavenCefAppHandlerAdapter;
import me.friwi.jcefmaven.UnsupportedPlatformException;
import org.cef.CefApp;
import org.cef.CefClient;
import org.cef.CefSettings;
import org.cef.browser.CefBrowser;
import org.cef.browser.CefMessageRouter;
import org.cef.handler.CefFocusHandlerAdapter;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;

public class WebFrame extends JFrame {

    private CefApp app;
    private Component ui;
    private CefClient client;
    private CefBrowser browser;
    private boolean browserFocus;
    private CefAppBuilder builder;

    public WebFrame(String url, String jcefPath) {
        try {
            init(url, jcefPath);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public CefApp getApp() {
        return app;
    }

    public Component getUI() {
        return ui;
    }

    public CefClient getClient() {
        return client;
    }

    public boolean browserHasFocus() {
        return browserFocus;
    }

    public CefBrowser getBrowser() {
        return browser;
    }

    public CefAppBuilder getBuilder() {
        return builder;
    }

    private void init(String url, String jcefPath) throws UnsupportedPlatformException, IOException, CefInitializationException, InterruptedException {
        browserFocus = true;
        File installDir = new File(jcefPath);
        ShadeMeBaby.getLogger().debug("NEED_JCEF_INSTALL: "+installDir.mkdirs());
        builder = new CefAppBuilder();
        builder.setAppHandler(new MavenCefAppHandlerAdapter() {
            @Override @Deprecated
            public void stateHasChanged(CefApp.CefAppState state) {
                if (state == CefApp.CefAppState.TERMINATED) Platform.exit();
            }
        });
        builder.getCefSettings().log_severity = CefSettings.LogSeverity.LOGSEVERITY_DISABLE;
        builder.setInstallDir(installDir);
        builder.install();
        builder.getCefSettings().windowless_rendering_enabled = false;
        app = builder.build();
        client = app.createClient();
        CefMessageRouter messageRouter = CefMessageRouter.create();
        client.addMessageRouter(messageRouter);
        browser = client.createBrowser(url, false, false);
        ui = browser.getUIComponent();
        client.addFocusHandler(new CefFocusHandlerAdapter() {
            @Override
            public void onGotFocus(CefBrowser browser) {
                if (browserFocus) return;
                browserFocus = true;
                KeyboardFocusManager.getCurrentKeyboardFocusManager().clearGlobalFocusOwner();
                browser.setFocus(true);
            }
            @Override
            public void onTakeFocus(CefBrowser browser, boolean next) {
                browserFocus = false;
            }
        });
        getContentPane().add(ui,BorderLayout.CENTER);
        pack();
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                CefApp.getInstance().dispose();
                dispose();
            }
        });
    }
}