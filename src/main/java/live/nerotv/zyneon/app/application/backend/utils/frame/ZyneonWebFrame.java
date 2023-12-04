package live.nerotv.zyneon.app.application.backend.utils.frame;

import live.nerotv.Main;
import live.nerotv.zyneon.app.application.backend.utils.backend.connector.BackendConnector;
import live.nerotv.zyneon.app.application.backend.utils.backend.connector.BackendConnectorV3;
import org.cef.CefSettings;
import org.cef.browser.CefBrowser;
import org.cef.handler.CefDisplayHandlerAdapter;

public class ZyneonWebFrame extends WebFrame {

    BackendConnector backendConnector;

    public ZyneonWebFrame(String url) {
        //super("https://danieldieeins.github.io/ZyneonApplicationContent/h/PB6/index.html",Main.getDirectoryPath()+"libs/jcef");
        super(url,Main.getDirectoryPath()+"libs/jcef");
        backendConnector = new BackendConnectorV3(this);
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
        setSize(1420,820);
        setResizable(false);
        setLocationRelativeTo(null);
    }

    public void openModrinth() {

    }

    public BackendConnector getBackendConnector() {
        return backendConnector;
    }
}