package live.nerotv.zyneon.app.frontend;

import live.nerotv.Main;
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

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;

public class JCefFrame extends JFrame {

    private final CefApp app;
    private Component ui;
    private final CefClient client;
    private final CefBrowser browser;
    private boolean browserFocus;
    private final CefAppBuilder builder;
    private final CefMessageRouter messageRouter;
    private final BackendConnectorV2 backendConnector;

    public JCefFrame(String url) throws UnsupportedPlatformException, CefInitializationException, IOException, InterruptedException {
        backendConnector = new BackendConnectorV2();
        browserFocus = true;
        File installDir = new File(Main.getDirectoryPath()+"libs/jcef/");
        System.out.println("NEED_JCEF_INSTALL: "+installDir.mkdirs());
        builder = new CefAppBuilder();
        builder.setAppHandler(new MavenCefAppHandlerAdapter() {
            @Override @Deprecated
            public void stateHasChanged(CefApp.CefAppState state) {
                if (state == CefApp.CefAppState.TERMINATED) System.exit(0);
            }
        });
        builder.setInstallDir(installDir);
        builder.install();
        builder.getCefSettings().windowless_rendering_enabled = false;
        app = builder.build();
        client = app.createClient();
        messageRouter = CefMessageRouter.create();
        client.addMessageRouter(messageRouter);
        if(url==null) {
            url = getClass().getResource("/index.html").toExternalForm();
        }
        browser = client.createBrowser(url, false, false);
        client.addDisplayHandler(new CefDisplayHandlerAdapter() {
            @Override
            public boolean onConsoleMessage(CefBrowser browser, CefSettings.LogSeverity level, String message, String source, int line) {
                if(message.contains("[Launcher-Bridge] ")) {
                    String request = message.replace("[Launcher-Bridge] ","");
                    System.out.println("BackendConnectorV2: Received request: \""+request+"\"");
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

    public CefClient getClient() {
        return client;
    }

    public CefBrowser getBrowser() {
        return browser;
    }

    public boolean isBrowserFocusActive() {
        return browserFocus;
    }

    public CefAppBuilder getBuilder() {
        return builder;
    }

    public CefMessageRouter getMessageRouter() {
        return messageRouter;
    }

    public BackendConnectorV2 getBackendConnector() {
        return backendConnector;
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
        setSize(1280,800);
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