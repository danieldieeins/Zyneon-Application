package live.nerotv.zyneon.app.application.backend.utils.backend.connector;

import live.nerotv.Main;

public interface BackendConnector {

    default void resolveRequest(String request) {
        Main.getLogger().log("Received request: " + request);
    }
}
