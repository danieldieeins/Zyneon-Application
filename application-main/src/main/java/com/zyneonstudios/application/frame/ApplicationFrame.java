package com.zyneonstudios.application.frame;

import com.zyneonstudios.application.main.ApplicationConfig;
import live.nerotv.shademebaby.frame.WebFrame;
import org.cef.CefSettings;
import org.cef.browser.CefBrowser;
import org.cef.handler.CefDisplayHandlerAdapter;

import java.awt.*;
import java.util.concurrent.CompletableFuture;

public class ApplicationFrame extends WebFrame {

    private final FrameConnector connector;

    public ApplicationFrame(String url, String jcefPath) {
        super(url, jcefPath);
        this.connector = new FrameConnector(this);
        getClient().addDisplayHandler(new CefDisplayHandlerAdapter() {
            @Override
            public boolean onConsoleMessage(CefBrowser browser, CefSettings.LogSeverity level, String message, String source, int line) {
                if (message.contains("[CONNECTOR] ")) {
                    CompletableFuture.runAsync(() -> {
                        String request = message.replace("[CONNECTOR] ", "");
                        connector.resolveRequest(request);
                    });
                }
                return super.onConsoleMessage(browser, level, message, source, line);
            }
        });
        setMinimumSize(new Dimension(840,500));
        setSize(new Dimension(1200,720));
        setLocationRelativeTo(null);
        setTitle("Zyneon Application");
    }

    public FrameConnector getConnector() {
        return connector;
    }

    public void setTitle(String title, Color background, Color foreground) {
        setTitle("Zyneon Application ("+title+", "+ ApplicationConfig.getOS()+")");
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
        getBrowser().executeJavaScript(command,getBrowser().getURL(),5);
    }
}