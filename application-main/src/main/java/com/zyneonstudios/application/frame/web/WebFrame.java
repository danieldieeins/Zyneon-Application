package com.zyneonstudios.application.frame.web;

import com.zyneonstudios.application.main.ApplicationConfig;
import com.zyneonstudios.application.main.NexusApplication;
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

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.Objects;

@SuppressWarnings("unused")
public class WebFrame extends JFrame {

    private CefApp app;
    private CefClient client;
    private CefBrowser browser;
    private boolean browserFocus;

    public WebFrame(String url, String jcefPath, NexusApplication application) {
        try {
            init(url, jcefPath, application);
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

    private void init(String url, String jcefPath, NexusApplication application) throws UnsupportedPlatformException, IOException, CefInitializationException, InterruptedException {
        browserFocus = true;
        File installDir = new File(jcefPath);
        ShadeMeBaby.getLogger().debug("[WEBFRAME] Is jCef installed: "+!installDir.mkdirs());
        CefAppBuilder builder = new CefAppBuilder();
        builder.setAppHandler(new MavenCefAppHandlerAdapter() {
            @Override @Deprecated
            public void stateHasChanged(CefApp.CefAppState state) {
                if (state == CefApp.CefAppState.TERMINATED) {
                    NexusApplication.stop();
                }
                if(!ApplicationConfig.getOS().startsWith("Windows")) {
                    if(state == CefApp.CefAppState.SHUTTING_DOWN) {
                        NexusApplication.stop();
                    }
                }
            }
        });
        setIconImage(ImageIO.read(Objects.requireNonNull(getClass().getResource("/logo.png"))).getScaledInstance(32, 32, Image.SCALE_SMOOTH));
        builder.getCefSettings().cache_path = (jcefPath+"/cache").replace("\\\\","\\").replace("//","/");
        builder.getCefSettings().log_severity = CefSettings.LogSeverity.LOGSEVERITY_DISABLE;
        builder.getCefSettings().persist_session_cookies = true;
        builder.setInstallDir(installDir);
        builder.install();
        builder.getCefSettings().windowless_rendering_enabled = false;
        app = builder.build();

        client = app.createClient();
        client.addDownloadHandler(new CefDownloadHandler() {
            @Override
            public void onBeforeDownload(CefBrowser browser, CefDownloadItem item, String sourceURL, CefBeforeDownloadCallback callback) {
                if(Desktop.isDesktopSupported()) {
                    try {
                        Desktop.getDesktop().browse(new URI(item.getURL()));
                    } catch (Exception ignore) {}
                }
            }

            @Override
            public void onDownloadUpdated(CefBrowser cefBrowser, CefDownloadItem cefDownloadItem, CefDownloadItemCallback cefDownloadItemCallback) {
                // Downloadfortschrittsaktualisierungen behandeln (optional)
            }
        });
        CefMessageRouter messageRouter = CefMessageRouter.create();
        if(client==null) {
            NexusApplication.getLogger().error("[WEBFRAME] Couldn't initialize WebFrame: CefClient client can't be null!");
            System.exit(-1); return;
        }
        client.addMessageRouter(messageRouter);
        browser = client.createBrowser(url, false, false);
        client.addDragHandler((cefBrowser, dragData, i) -> dragData.isFile());
        if(browser==null) {
            NexusApplication.getLogger().error("[WEBFRAME] Couldn't initialize WebFrame: CefBrowser browser can't be null!");
            System.exit(-1); return;
        }
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
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                CefApp.getInstance().dispose();
                dispose();
            }
        });
    }

    public void executeJavaScript(String command) {
        browser.executeJavaScript(command,browser.getURL(),5);
    }
}
