package live.nerotv.zyneon.app.frontend.deprecated.jcef;

import live.nerotv.zyneon.app.frontend.deprecated.BackendConnector;
import me.friwi.jcefmaven.CefAppBuilder;
import me.friwi.jcefmaven.CefInitializationException;
import me.friwi.jcefmaven.MavenCefAppHandlerAdapter;
import me.friwi.jcefmaven.UnsupportedPlatformException;
import org.cef.CefApp;
import org.cef.CefApp.CefAppState;
import org.cef.CefClient;
import org.cef.browser.CefBrowser;
import org.cef.browser.CefMessageRouter;
import org.cef.handler.CefFocusHandlerAdapter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.Serial;

@Deprecated
public class MainFrame extends JFrame {

    @Serial @Deprecated
    private static final long serialVersionUID = -5570653778104813836L;

    @Deprecated
    private boolean browserFocus_ = true;

    @Deprecated
    private BackendConnector backendConnector;

    @Deprecated
    public MainFrame(String startURL, boolean useOSR, boolean isTransparent, String[] args) throws UnsupportedPlatformException, CefInitializationException, IOException, InterruptedException {
        backendConnector = new BackendConnector();

        CefAppBuilder builder = new CefAppBuilder();
        builder.getCefSettings().windowless_rendering_enabled = useOSR;
        builder.setAppHandler(new MavenCefAppHandlerAdapter() {
            @Override @Deprecated
            public void stateHasChanged(CefAppState state) {
                if (state == CefAppState.TERMINATED) System.exit(0);
            }
        });
        if (args.length > 0) {
            builder.addJcefArgs(args);
        }
        CefApp cefApp_ = builder.build();
        CefClient client_ = cefApp_.createClient();
        CefMessageRouter msgRouter = CefMessageRouter.create();
        client_.addMessageRouter(msgRouter);
        CefBrowser browser_ = client_.createBrowser(startURL, useOSR, isTransparent);

        Component browerUI_ = browser_.getUIComponent();
        client_.addFocusHandler(new CefFocusHandlerAdapter() {
            @Override @Deprecated
            public void onGotFocus(CefBrowser browser) {
                if (browserFocus_) return;
                browserFocus_ = true;
                KeyboardFocusManager.getCurrentKeyboardFocusManager().clearGlobalFocusOwner();
                browser.setFocus(true);
            }

            @Override @Deprecated
            public void onTakeFocus(CefBrowser browser, boolean next) {
                browserFocus_ = false;
            }
        });

        getContentPane().add(browerUI_, BorderLayout.CENTER);
        pack();
        setSize(800, 600);
        setVisible(true);
        addWindowListener(new WindowAdapter() {
            @Override @Deprecated
            public void windowClosing(WindowEvent e) {
                CefApp.getInstance().dispose();
                dispose();
            }
        });
    }

    @Deprecated
    public static void main(String[] args) throws UnsupportedPlatformException, CefInitializationException, IOException, InterruptedException {
        TestReportGenerator.print(args);
        boolean useOsr = false;
        new MainFrame("http://www.google.com", useOsr, false, args);
    }
}