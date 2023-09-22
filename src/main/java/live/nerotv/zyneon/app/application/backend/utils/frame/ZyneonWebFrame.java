package live.nerotv.zyneon.app.application.backend.utils.frame;

import javafx.application.Platform;
import live.nerotv.Main;
import live.nerotv.openlauncherapi.auth.SimpleMicrosoftAuth;
import live.nerotv.shademebaby.frame.NFrame;
import live.nerotv.zyneon.app.application.backend.utils.backend.connector.BackendConnectorV3;
import me.friwi.jcefmaven.CefAppBuilder;
import me.friwi.jcefmaven.CefInitializationException;
import me.friwi.jcefmaven.MavenCefAppHandlerAdapter;
import me.friwi.jcefmaven.UnsupportedPlatformException;
import org.cef.CefApp;
import org.cef.CefClient;
import org.cef.CefSettings;
import org.cef.browser.CefBrowser;
import org.cef.browser.CefMessageRouter;
import org.cef.handler.CefDisplayHandlerAdapter;
import org.cef.handler.CefFocusHandlerAdapter;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;

public class ZyneonWebFrame extends NFrame {

    private final CefApp app;
    private Component ui;
    private final CefClient client;
    private final CefBrowser browser;
    private boolean browserFocus;
    private final CefAppBuilder builder;
    private final BackendConnectorV3 backendConnector;

    public ZyneonWebFrame(SimpleMicrosoftAuth auth) throws UnsupportedPlatformException, CefInitializationException, IOException, InterruptedException {
        backendConnector = new BackendConnectorV3(auth,this);
        browserFocus = true;
        File installDir = new File(Main.getDirectoryPath()+"libs/jcef/");
        Main.debug("NEED_JCEF_INSTALL: "+installDir.mkdirs());
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
        browser = client.createBrowser("https://danieldieeins.github.io/ZyneonApplicationContent/h/index.html", false, false);
        client.addDisplayHandler(new CefDisplayHandlerAdapter() {
            @Override
            public boolean onConsoleMessage(CefBrowser browser, CefSettings.LogSeverity level, String message, String source, int line) {
                if(message.contains("[Launcher-Bridge] ")) {
                    String request = message.replace("[Launcher-Bridge] ","");
                    Main.debug("[BackendConnector] Received request: \""+request+"\"");
                    backendConnector.resolveRequest(request);
                }
                return super.onConsoleMessage(browser, level, message, source, line);
            }
        });
    }

    public CefApp getApp() {
        return app;
    }

    public Component getUI() {
        return ui;
    }

    public CefBrowser getBrowser() {
        return browser;
    }

    public CefAppBuilder getBuilder() {
        return builder;
    }

    public void open() {
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
        setSize(1280,820);
        setVisible(true);
        addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                CefApp.getInstance().dispose();
                dispose();
            }

        });
    }
}