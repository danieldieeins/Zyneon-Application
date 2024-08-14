package com.zyneonstudios.application.frame.web;

import com.zyneonstudios.application.frame.FrameConnector;
import com.zyneonstudios.application.main.ApplicationStorage;
import com.zyneonstudios.application.main.NexusApplication;
import org.cef.CefSettings;
import org.cef.browser.CefBrowser;
import org.cef.handler.CefDisplayHandlerAdapter;

import java.awt.*;
import java.util.concurrent.CompletableFuture;

@SuppressWarnings("unused")
public class ApplicationFrame extends WebFrame {

    private final FrameConnector connector;

    public ApplicationFrame(NexusApplication application, String url, String jcefPath) {
        super(url, jcefPath, application);
        this.connector = new FrameConnector(this,application);
        getClient().addDisplayHandler(new CefDisplayHandlerAdapter() {
            @Override
            public boolean onConsoleMessage(CefBrowser browser, CefSettings.LogSeverity level, String message, String source, int line) {
                if (message.startsWith("[CONNECTOR] ")) {
                    CompletableFuture.runAsync(() -> {
                        String request = message.replace("[CONNECTOR] ", "");
                        connector.resolveRequest(request);
                    });
                } else if (message.startsWith("[LOG] ")) {
                    NexusApplication.getLogger().log(message.replace("[LOG] ",""));
                } else if (message.startsWith("[ERR] ")) {
                    NexusApplication.getLogger().error(message.replace("[ERR] ",""));
                } else if (message.startsWith("[DEB] ")) {
                    NexusApplication.getLogger().debug(message.replace("[DEB] ",""));
                }
                return super.onConsoleMessage(browser, level, message, source, line);
            }
        });
        setMinimumSize(new Dimension(840,500));
    }

    public void setTitlebar(String title, Color background, Color foreground) {
        //setTitle("Zyneon Application ("+title+", v"+ ApplicationStorage.getApplicationVersion()+", "+ ApplicationStorage.getOS()+")");
        setTitle("Zyneon Application");
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

    public void openCustomPage(String title, String pageId, String url) {
        getBrowser().loadURL(ApplicationStorage.urlBase+ ApplicationStorage.language+"/custom.html?title="+title+"&id="+pageId+"&url="+url);
    }
}
