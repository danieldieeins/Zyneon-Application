package live.nerotv;

import live.nerotv.zyneon.app.frontend.WebViewApp;

public class Main {

    private static WebViewApp mainWindow;
    private static String[] arguments;

    public static void main(String[] args) {
        arguments = args;
        mainWindow = new WebViewApp();
        mainWindow.start(args);
    }

    public static WebViewApp getMainWindow() {
        return mainWindow;
    }

    public static String[] getArguments() {
        return arguments;
    }
}