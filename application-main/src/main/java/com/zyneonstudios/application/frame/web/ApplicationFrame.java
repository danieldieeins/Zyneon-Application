package com.zyneonstudios.application.frame.web;

import com.zyneonstudios.application.frame.FrameConnector;
import com.zyneonstudios.application.main.ApplicationStorage;
import com.zyneonstudios.application.main.NexusApplication;
import org.cef.CefSettings;
import org.cef.browser.CefBrowser;
import org.cef.browser.CefFrame;
import org.cef.handler.CefDisplayHandlerAdapter;
import org.cef.handler.CefLoadHandler;
import org.cef.network.CefRequest;

import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.concurrent.CompletableFuture;

@SuppressWarnings("unused")
public class ApplicationFrame extends WebFrame implements ComponentListener {

    private final FrameConnector connector;
    private final Dimension minSize = new Dimension(640,360);

    public ApplicationFrame(NexusApplication application, String url, String jcefPath) {
        super(url, jcefPath, application);
        addComponentListener(this);
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
                } else {
                    NexusApplication.getLogger().debug("[FRAME] (Console) "+message);
                }
                return super.onConsoleMessage(browser, level, message, source, line);
            }
        });
        getClient().addLoadHandler(new CefLoadHandler() {
            @Override
            public void onLoadingStateChange(CefBrowser cefBrowser, boolean b, boolean b1, boolean b2) {

            }

            @Override
            public void onLoadStart(CefBrowser cefBrowser, CefFrame cefFrame, CefRequest.TransitionType transitionType) {
                double zoomLevel = ApplicationStorage.getZoomLevel();
                if (getWidth() < 700 || getHeight() < 480) {
                    zoomLevel -= 2;
                } else if (getWidth() < 1080 || getHeight() < 720) {
                    zoomLevel -= 1;
                }
                getBrowser().setZoomLevel(zoomLevel);
            }

            @Override
            public void onLoadEnd(CefBrowser cefBrowser, CefFrame cefFrame, int i) {

            }

            @Override
            public void onLoadError(CefBrowser cefBrowser, CefFrame cefFrame, ErrorCode errorCode, String s, String s1) {

            }
        });
        setMinimumSize(minSize);
    }

    public Dimension getMinSize() {
        return minSize;
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

    @Override
    public void componentResized(ComponentEvent e) {
        double zoomLevel = ApplicationStorage.getZoomLevel();
        if (getWidth() < 700 || getHeight() < 425) {
            zoomLevel -= 2;
        } else if (getWidth() < 1080 || getHeight() < 525) {
            zoomLevel -= 1;
        }
        getBrowser().setZoomLevel(zoomLevel);
    }

    @Override
    public void componentMoved(ComponentEvent e) {

    }

    @Override
    public void componentShown(ComponentEvent e) {

    }

    @Override
    public void componentHidden(ComponentEvent e) {

    }
}
