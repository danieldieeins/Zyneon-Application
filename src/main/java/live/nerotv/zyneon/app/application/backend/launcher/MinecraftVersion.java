package live.nerotv.zyneon.app.application.backend.launcher;

public class MinecraftVersion {

    public static Type getType(String version) {
        switch(version) {
            case "1.14", "1.14.0", "1.14.1", "1.14.2", "1.14.3", "1.14.4", "1.15.0", "1.15.1", "1.15.2", "1.16", "1.16.0", "1.16.1", "1.16.2", "1.16.3", "1.16.4", "1.16.5" -> {
                return Type.SEMI_NEW;
            }
            case "1.17", "1.17.0", "1.17.1", "1.18", "1.18.0", "1.18.1", "1.18.2", "1.19", "1.19.0", "1.19.1", "1.19.2", "1.19.4", "1.20", "1.20.0", "1.20.1", "1.20.2", "1.20.3", "1.20.4", "1.20.5" -> {
                return Type.NEW;
            }
        }
        try {
            int i = Integer.parseInt(version.split("\\.")[1]);
            if(i<13) {
                return Type.LEGACY;
            } else if(i>20) {
                return Type.NEW;
            }
        } catch (Exception ignore) {}
        return null;
    }

    public enum Type {
        LEGACY,
        SEMI_NEW,
        NEW
    }
}
