package live.nerotv;

import live.nerotv.zyneon.launcher.frontend.WebViewApp;

public class Main {

    private static WebViewApp mainWindow;

    public static void main(String[] args) {
        mainWindow = new WebViewApp();
        mainWindow.start(args);
    }

    public static WebViewApp getMainWindow() {
        return mainWindow;
    }
}