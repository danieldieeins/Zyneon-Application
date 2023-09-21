package live.nerotv.zyneon.app.application.backend.utils.backend.connector;

public interface BackendConnector {

    default void resolveRequest(String request) {
        System.out.println("Received request: "+request);
    }
}
