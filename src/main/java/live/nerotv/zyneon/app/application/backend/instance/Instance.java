package live.nerotv.zyneon.app.application.backend.instance;

import live.nerotv.zyneon.app.application.backend.utils.file.Config;

public interface Instance {

    default boolean checkVersion() {
        return true;
    }

    default boolean update() {
        return true;
    }

    default Config getJSON() {
        return null;
    }

    default String getPath() {
        return null;
    }

    default String getID() {
        return null;
    }

    default String getName() {
        return null;
    }

    default String getVersion() {
        return null;
    }

    default String getMinecraftVersion() {
        return null;
    }
}