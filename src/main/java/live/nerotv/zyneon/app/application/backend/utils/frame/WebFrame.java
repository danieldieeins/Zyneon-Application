package live.nerotv.zyneon.app.application.backend.utils.frame;

import javafx.application.Platform;
import live.nerotv.Main;
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
import org.cef.callback.CefBeforeDownloadCallback;
import org.cef.callback.CefDownloadItem;
import org.cef.callback.CefDownloadItemCallback;
import org.cef.handler.CefDownloadHandler;
import org.cef.handler.CefFocusHandlerAdapter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;

public class WebFrame extends JFrame {

    private CefApp app;
    private CefClient client;
    private CefBrowser browser;
    private boolean browserFocus;

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

    public WebFrame getInstance() {
        return this;
    }

    public CefClient getClient() {
        return client;
    }

    public CefBrowser getBrowser() {
        return browser;
    }

    private void init(String url, String jcefPath) throws UnsupportedPlatformException, IOException, CefInitializationException, InterruptedException {
        browserFocus = true;
        File installDir = new File(jcefPath);
        ShadeMeBaby.getLogger().debug("NEED_JCEF_INSTALL: "+installDir.mkdirs());
        CefAppBuilder builder = new CefAppBuilder();
        builder.setAppHandler(new MavenCefAppHandlerAdapter() {
            @Override @Deprecated
            public void stateHasChanged(CefApp.CefAppState state) {
                if (state == CefApp.CefAppState.TERMINATED) Platform.exit();
            }
        });
        File cache = new File(Main.getDirectoryPath()+"libs/jcef/cache/");
        if(cache.mkdirs()) {
            Main.getLogger().debug("JCEF cache: "+cache.getAbsolutePath());
        }
        builder.getCefSettings().cache_path = Main.getDirectoryPath()+"libs/jcef/cache/";
        builder.getCefSettings().log_severity = CefSettings.LogSeverity.LOGSEVERITY_DISABLE;
        builder.getCefSettings().persist_session_cookies = true;
        builder.setInstallDir(installDir);
        builder.install();
        builder.getCefSettings().windowless_rendering_enabled = false;
        app = builder.build();
        client = app.createClient();
        CefMessageRouter messageRouter = CefMessageRouter.create();
        client.addMessageRouter(messageRouter);
        client.addDownloadHandler(new CefDownloadHandler() {
            @Override
            public void onBeforeDownload(CefBrowser browser, CefDownloadItem item, String s, CefBeforeDownloadCallback callback) {
                callback.Continue(s,true);
            }

            @Override
            public void onDownloadUpdated(CefBrowser cefBrowser, CefDownloadItem cefDownloadItem, CefDownloadItemCallback cefDownloadItemCallback) {

            }
        });
        browser = client.createBrowser(url, false, false);
        client.addDragHandler((cefBrowser, dragData, i) -> dragData.isFile());
        Component ui = browser.getUIComponent();
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

    public void setTitlebar(String title, Color background, Color foreground) {
        setTitle(title);
        setTitleBackground(background);
        setTitleForeground(foreground);
    }
    public void setTitleBackground(Color color) {
        setBackground(color);
        getRootPane().putClientProperty("JRootPane.titleBarBackground", color);
    }

    public void setTitleForeground(Color color) {
        getRootPane().putClientProperty("JRootPane.titleBarForeground", color);
    }

    public void executeJavaScript(String command) {
        browser.executeJavaScript(command,browser.getURL(),5);
    }
}