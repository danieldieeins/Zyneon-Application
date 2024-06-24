package com.zyneonstudios.application.frame.web;

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
import java.util.Objects;

public class WebFrame extends JFrame {

    /*
     * Zyneon Application web frame
     * by nerotvlive
     * Contributions are welcome. Please add your name to the "by" line if you make any modifications.
     * */

    // Instance variables
    private CefApp app; // CEF application instance
    private CefClient client; // CEF client instance
    private CefBrowser browser; // CEF browser instance
    private boolean browserFocus; // Flag indicating whether browser has focus

    // Constructor
    public WebFrame(String url, String jcefPath, NexusApplication application) {
        try {
            init(url, jcefPath, application); // Initialize WebFrame
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // Getter methods
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

    // Initialization method
    private void init(String url, String jcefPath, NexusApplication application) throws UnsupportedPlatformException, IOException, CefInitializationException, InterruptedException {
        browserFocus = true; // Initially, browser has focus
        File installDir = new File(jcefPath); // Directory where jCEF is installed
        ShadeMeBaby.getLogger().debug("[WEBFRAME] Is jCef installed: "+!installDir.mkdirs()); // Log whether jCEF is installed
        CefAppBuilder builder = new CefAppBuilder(); // Builder for creating CEF application
        // Set up app handler to handle application state changes
        builder.setAppHandler(new MavenCefAppHandlerAdapter() {
            @Override @Deprecated
            public void stateHasChanged(CefApp.CefAppState state) {
                if (state == CefApp.CefAppState.TERMINATED) {
                    NexusApplication.stop();
                }
            }
        });
        setIconImage(ImageIO.read(Objects.requireNonNull(getClass().getResource("/logo.png"))).getScaledInstance(32, 32, Image.SCALE_SMOOTH));
        builder.getCefSettings().cache_path = (jcefPath+"/cache").replace("\\\\","\\").replace("//","/"); // Set cache path for CEF
        builder.getCefSettings().log_severity = CefSettings.LogSeverity.LOGSEVERITY_DISABLE; // Disable logging
        builder.getCefSettings().persist_session_cookies = true; // Persist session cookies
        builder.setInstallDir(installDir); // Set installation directory for jCEF
        builder.install(); // Install jCEF
        builder.getCefSettings().windowless_rendering_enabled = false; // Enable windowed rendering
        app = builder.build(); // Build CEF application
        client = app.createClient(); // Create CEF client
        CefMessageRouter messageRouter = CefMessageRouter.create(); // Create message router
        client.addMessageRouter(messageRouter); // Add message router to client
        // Set up download handler to handle file downloads
        client.addDownloadHandler(new CefDownloadHandler() {
            @Override
            public void onBeforeDownload(CefBrowser browser, CefDownloadItem item, String s, CefBeforeDownloadCallback callback) {
                callback.Continue(s,true); // Continue the download
            }

            @Override
            public void onDownloadUpdated(CefBrowser cefBrowser, CefDownloadItem cefDownloadItem, CefDownloadItemCallback cefDownloadItemCallback) {
                // Method not implemented, as we're not handling download updates
            }
        });
        browser = client.createBrowser(url, false, false); // Create browser instance
        // Set up drag handler to handle file drag events
        client.addDragHandler((cefBrowser, dragData, i) -> dragData.isFile());
        Component ui = browser.getUIComponent(); // Get UI component of browser
        // Set up focus handler to handle focus events
        client.addFocusHandler(new CefFocusHandlerAdapter() {
            @Override
            public void onGotFocus(CefBrowser browser) {
                if (browserFocus) return; // If browser already has focus, return
                browserFocus = true; // Update flag to indicate browser has focus
                KeyboardFocusManager.getCurrentKeyboardFocusManager().clearGlobalFocusOwner(); // Clear global focus owner
                browser.setFocus(true); // Set focus to browser
            }
            @Override
            public void onTakeFocus(CefBrowser browser, boolean next) {
                browserFocus = false; // Browser lost focus
            }
        });
        getContentPane().add(ui,BorderLayout.CENTER); // Add UI component to frame
        // Add window listener to handle window closing event
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                CefApp.getInstance().dispose(); // Dispose CEF instance
                dispose(); // Dispose frame
            }
        });
    }

    // Method to execute JavaScript commands in the browser
    public void executeJavaScript(String command) {
        browser.executeJavaScript(command,browser.getURL(),5);
    }
}
