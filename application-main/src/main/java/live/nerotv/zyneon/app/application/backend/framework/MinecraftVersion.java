package live.nerotv.zyneon.app.application.backend.framework;

import java.util.ArrayList;

public class MinecraftVersion {

    public static ArrayList<String> supportedVersions = new ArrayList<>();

    public static void syncVersions() {
        supportedVersions = new ArrayList<>();
        supportedVersions.add("1.20.4 (Latest)");
        supportedVersions.add("1.20.3");
        supportedVersions.add("1.20.2");
        supportedVersions.add("1.20.1");
        supportedVersions.add("1.20");

        supportedVersions.add("1.19.4");
        supportedVersions.add("1.19.3");
        supportedVersions.add("1.19.2");
        supportedVersions.add("1.19.1");
        supportedVersions.add("1.19");

        supportedVersions.add("1.18.2");
        supportedVersions.add("1.18.1");
        supportedVersions.add("1.18");

        supportedVersions.add("1.17.1");
        supportedVersions.add("1.17");

        supportedVersions.add("1.16.5");
        supportedVersions.add("1.16.4");
        supportedVersions.add("1.16.3");
        supportedVersions.add("1.16.2");
        supportedVersions.add("1.16.1");
        supportedVersions.add("1.16");

        supportedVersions.add("1.15.2");
        supportedVersions.add("1.15.1");
        supportedVersions.add("1.15");

        supportedVersions.add("1.14.4");
        supportedVersions.add("1.14.3");
        supportedVersions.add("1.14.2");
        supportedVersions.add("1.14.1");
        supportedVersions.add("1.14");

        supportedVersions.add("1.13.2");
        supportedVersions.add("1.13.1");
        supportedVersions.add("1.13");

        supportedVersions.add("1.12.2");
        supportedVersions.add("1.12.1");
        supportedVersions.add("1.12");

        supportedVersions.add("1.11.2");
        supportedVersions.add("1.11.1");
        supportedVersions.add("1.11");

        supportedVersions.add("1.10.2");
        supportedVersions.add("1.10.1");
        supportedVersions.add("1.10");

        supportedVersions.add("1.9.4");
        supportedVersions.add("1.9.3");
        supportedVersions.add("1.9.2");
        supportedVersions.add("1.9.1");
        supportedVersions.add("1.9");

        supportedVersions.add("1.8.9");
        supportedVersions.add("1.8.8");
        supportedVersions.add("1.8.7");
        supportedVersions.add("1.8.6");
        supportedVersions.add("1.8.5");
        supportedVersions.add("1.8.4");
        supportedVersions.add("1.8.3");
        supportedVersions.add("1.8.2");
        supportedVersions.add("1.8.1");
        supportedVersions.add("1.8");

        supportedVersions.add("1.7.10");
        supportedVersions.add("1.7.9");
        supportedVersions.add("1.7.8");
        supportedVersions.add("1.7.7");
        supportedVersions.add("1.7.6");
        supportedVersions.add("1.7.5");
        supportedVersions.add("1.7.4");
        supportedVersions.add("1.7.3");

        //UNSUPPORTED VERSIONS 1.6 - 1.7.2

        supportedVersions.add("1.5.2");
        supportedVersions.add("1.5.1");
        supportedVersions.add("1.5");

        supportedVersions.add("1.4.7");
        supportedVersions.add("1.4.6");
        supportedVersions.add("1.4.5");
        supportedVersions.add("1.4.4");
        supportedVersions.add("1.4.1");
        supportedVersions.add("1.4");

        supportedVersions.add("1.3.2");
        supportedVersions.add("1.3.1");

        supportedVersions.add("1.2.5");
        supportedVersions.add("1.2.4");
        supportedVersions.add("1.2.3");
        supportedVersions.add("1.2.1");
    }

    public static Type getType(String version) {
        int i = Integer.parseInt(version.split("\\.")[1]);
        if(i<18) {
            System.out.println(version+": LEGACY ("+i+")");
            return Type.LEGACY;
        } else {
            System.out.println(version+": NEW ("+i+")");
            return Type.NEW;
        }
    }

    public enum Type {
        LEGACY,
        SEMI_NEW,
        NEW
    }
}