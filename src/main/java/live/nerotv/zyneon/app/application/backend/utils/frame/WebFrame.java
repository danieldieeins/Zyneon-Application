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
    private static Point mouseDownCompCoords;

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

    public JPanel titlebar;

    public JLabel title;

    public JButton close;

    public JButton minimize;

    private void init(String url, String jcefPath) throws UnsupportedPlatformException, IOException, CefInitializationException, InterruptedException {
        browserFocus = true;


        this.setUndecorated(true);
        titlebar = new JPanel(new BorderLayout());
        titlebar.setBackground(Color.decode("#03000b"));

        close = new JButton("X");
        close.setBackground(Color.decode("#03000b"));
        close.setContentAreaFilled(true);
        close.setBorderPainted(false);
        close.setFocusPainted(false);
        close.addMouseListener(new MouseAdapter() {
            Color color = Color.decode("#03000b");
            public void mouseEntered(MouseEvent e) {
                color = close.getBackground();
                close.setBackground(Color.RED);
            }

            public void mouseExited(MouseEvent e) {
                close.setBackground(color);
            }
        });
        close.setForeground(Color.WHITE);
        close.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                getInstance().dispatchEvent(new WindowEvent(getInstance(), WindowEvent.WINDOW_CLOSING));
            }
        });

        minimize = new JButton("_");
        minimize.setBackground(Color.decode("#03000b"));
        minimize.setContentAreaFilled(true);
        minimize.setBorderPainted(false);
        minimize.setFocusPainted(false);
        minimize.addMouseListener(new MouseAdapter() {
            Color color = Color.decode("#03000b");
            public void mouseEntered(MouseEvent e) {
                color = minimize.getBackground();
                minimize.setBackground(Color.WHITE);
                minimize.setForeground(Color.BLACK);
            }

            public void mouseExited(MouseEvent e) {
                minimize.setBackground(color);
                minimize.setForeground(Color.WHITE);
            }
        });
        minimize.setForeground(Color.WHITE);
        minimize.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setState(Frame.ICONIFIED);
            }
        });

        title = new JLabel("   Zyneon Application", JLabel.LEFT);
        title.setForeground(Color.decode("#999999"));
        titlebar.add(title, BorderLayout.CENTER);

        JPanel buttons = new JPanel(new BorderLayout());
        buttons.add(minimize,BorderLayout.WEST);
        buttons.add(close,BorderLayout.EAST);

        titlebar.add(buttons,BorderLayout.EAST);

        titlebar.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                mouseDownCompCoords = e.getPoint();
            }
        });

        titlebar.addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) {
                Point currCoords = e.getLocationOnScreen();
                WebFrame.this.setLocation(currCoords.x - mouseDownCompCoords.x, currCoords.y - mouseDownCompCoords.y);
            }
        });


        this.getContentPane().add(titlebar,BorderLayout.NORTH);


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
}