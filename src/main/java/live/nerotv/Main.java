package live.nerotv;

import live.nerotv.zyneon.launcher.ZyneonLauncher;
import live.nerotv.zyneon.launcher.frontend.WebViewApp;

public class Main {

    private static ZyneonLauncher launcher;

    public static void main(String[] args) {
        WebViewApp.main(args);
        launcher = new ZyneonLauncher();
        launcher.start();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            launcher.stop();
        }));
    }

    public static ZyneonLauncher getLauncher() {
        return launcher;
    }
}