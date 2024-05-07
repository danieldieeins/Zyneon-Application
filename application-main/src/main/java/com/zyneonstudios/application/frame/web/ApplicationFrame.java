package com.zyneonstudios.application.frame.web;

import com.zyneonstudios.application.frame.FrameConnector;
import com.zyneonstudios.application.main.ApplicationConfig;
import com.zyneonstudios.application.main.NexusApplication;
import org.cef.CefSettings;
import org.cef.browser.CefBrowser;
import org.cef.handler.CefDisplayHandlerAdapter;

import java.awt.*;
import java.util.concurrent.CompletableFuture;

public class ApplicationFrame extends WebFrame {

    /*
     * Zyneon Application default application frame
     * by nerotvlive
     * Contributions are welcome. Please add your name to the "by" line if you make any modifications.
     * */

    private final FrameConnector connector; // Instance of FrameConnector for handling frame requests

    // Constructor
    public ApplicationFrame(String url, String jcefPath) {
        super(url, jcefPath); // Call superclass constructor
        this.connector = new FrameConnector(this); // Initialize FrameConnector
        getClient().addDisplayHandler(new CefDisplayHandlerAdapter() {
            // Override method to handle console messages
            @Override
            public boolean onConsoleMessage(CefBrowser browser, CefSettings.LogSeverity level, String message, String source, int line) {
                // Check message prefix to determine the type of message
                if (message.startsWith("[CONNECTOR] ")) {
                    CompletableFuture.runAsync(() -> {
                        String request = message.replace("[CONNECTOR] ", "");
                        connector.resolveRequest(request); // Resolve connector request asynchronously
                    });
                } else if (message.startsWith("[LOG] ")) {
                    NexusApplication.getLogger().log(message.replace("[LOG] ","")); // Log message
                } else if (message.startsWith("[ERR] ")) {
                    NexusApplication.getLogger().error(message.replace("[ERR] ","")); // Log error
                } else if (message.startsWith("[DEB] ")) {
                    NexusApplication.getLogger().debug(message.replace("[DEB] ","")); // Log debug message
                }
                return super.onConsoleMessage(browser, level, message, source, line); // Call superclass method
            }
        });
        setMinimumSize(new Dimension(840,500)); // Set minimum size for the frame
    }

    // Getter method for FrameConnector
    public FrameConnector getConnector() {
        return connector;
    }

    // Method to set title bar properties
    public void setTitlebar(String title, Color background, Color foreground) {
        setTitle("Zyneon Application ("+title+", "+ ApplicationConfig.getOS()+")"); // Set frame title
        setTitleBackground(background); // Set title bar background color
        setTitleForeground(foreground); // Set title bar foreground color
    }

    // Method to set title bar background color
    public void setTitleBackground(Color color) {
        setBackground(color); // Set frame background color
        getRootPane().putClientProperty("JRootPane.titleBarBackground", color); // Set title bar background color
    }

    // Method to set title bar foreground color
    public void setTitleForeground(Color color) {
        getRootPane().putClientProperty("JRootPane.titleBarForeground", color); // Set title bar foreground color
    }

    // Method to execute JavaScript commands in the browser
    public void executeJavaScript(String command) {
        getBrowser().executeJavaScript(command,getBrowser().getURL(),5); // Execute JavaScript command
    }
}
