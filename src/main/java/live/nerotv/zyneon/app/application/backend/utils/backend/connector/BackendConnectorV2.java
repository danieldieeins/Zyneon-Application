package live.nerotv.zyneon.app.application.backend.utils.backend.connector;

public interface BackendConnectorV2 extends BackendConnector {

    default boolean startInstance(String id) {
        return false;
    }

    default boolean startZyneonPlus(String versionID, int ram) {
        return false;
    }
}