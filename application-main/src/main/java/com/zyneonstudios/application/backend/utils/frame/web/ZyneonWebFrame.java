package com.zyneonstudios.application.backend.utils.frame.web;

import com.zyneonstudios.Main;
import com.zyneonstudios.application.backend.utils.backend.Connector;
import org.cef.CefSettings;
import org.cef.browser.CefBrowser;
import org.cef.handler.CefDisplayHandlerAdapter;

import java.awt.*;

public class ZyneonWebFrame extends WebFrame {

    private Connector connector;

    public ZyneonWebFrame(String url) {
        super(url, Main.getDirectoryPath()+"libs/jcef");
        connector = new Connector(this);
        getClient().addDisplayHandler(new CefDisplayHandlerAdapter() {
            @Override
            public boolean onConsoleMessage(CefBrowser browser, CefSettings.LogSeverity level, String message, String source, int line) {
                if(message.contains("[Launcher-Bridge] ")) {
                    String request = message.replace("[Launcher-Bridge] ","");
                    Main.getLogger().debug("[CONNECTOR] Received request: \""+request+"\"");
                    connector.resolveRequest(request);
                }
                return super.onConsoleMessage(browser, level, message, source, line);
            }
        });
    }

    public Connector getConnector() {
        return connector;
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
}