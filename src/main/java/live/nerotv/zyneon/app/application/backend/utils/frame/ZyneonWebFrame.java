package live.nerotv.zyneon.app.application.backend.utils.frame;

import live.nerotv.Main;
import live.nerotv.shademebaby.frame.WebFrame;
import live.nerotv.zyneon.app.application.backend.utils.backend.connector.BackendConnector;
import org.cef.CefSettings;
import org.cef.browser.CefBrowser;
import org.cef.handler.CefDisplayHandlerAdapter;

import java.awt.*;

public class ZyneonWebFrame extends WebFrame {

    public ZyneonWebFrame(String url) {
        super(url,Main.getDirectoryPath()+"libs/jcef");
        BackendConnector backendConnector = new BackendConnector(this);
        getClient().addDisplayHandler(new CefDisplayHandlerAdapter() {
            @Override
            public boolean onConsoleMessage(CefBrowser browser, CefSettings.LogSeverity level, String message, String source, int line) {
                if(message.contains("[Launcher-Bridge] ")) {
                    String request = message.replace("[Launcher-Bridge] ","");
                    Main.getLogger().debug("[BackendConnector] Received request: \""+request+"\"");
                    backendConnector.resolveRequest(request);
                }
                return super.onConsoleMessage(browser, level, message, source, line);
            }
        });
        setMinimumSize(new Dimension(960,500));
        setSize(new Dimension(1200,720));
        setResizable(true);
        setLocationRelativeTo(null);
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