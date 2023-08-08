package live.nerotv;

import live.nerotv.zyneon.launcher.ZyneonLauncher;

public class Main {

    private static ZyneonLauncher launcher;

    public static void main(String[] args) {
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