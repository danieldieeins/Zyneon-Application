package live.nerotv.zyneon.app.backend.login;

import fr.litarvan.openauth.microsoft.MicrosoftAuthenticator;
import javafx.scene.control.Alert;
import live.nerotv.Main;

import java.util.Base64;

public class MicrosoftAuth {

    public static void authenticateMS() {
        MicrosoftAuthenticator authenticator = new MicrosoftAuthenticator();
        authenticator.loginWithAsyncWebview().whenComplete((response, error) -> {
            if (error != null) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setContentText(error.getMessage());
                alert.show();
                return;
            }
            Main.config.set("account.access", Base64.getEncoder().encodeToString(response.getAccessToken().getBytes()));
            Main.config.set("account.refresh",Base64.getEncoder().encodeToString(response.getRefreshToken().getBytes()));
            Main.config.set("account.name", Base64.getEncoder().encodeToString(response.getProfile().getName().getBytes()));
            Main.config.set("account.id",Base64.getEncoder().encodeToString(response.getProfile().getId().getBytes()));
        });
    }
}