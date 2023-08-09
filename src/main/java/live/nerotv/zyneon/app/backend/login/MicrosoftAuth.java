package live.nerotv.zyneon.app.backend.login;

import fr.litarvan.openauth.microsoft.MicrosoftAuthenticator;
import javafx.application.Platform;
import javafx.scene.control.Alert;

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

            /*saver.set("msAccessToken", response.getAccessToken());
            saver.set("msRefreshToken", response.getRefreshToken());
            saver.save();
            Launcher.getInstance().setAuthInfos(new AuthInfos(
                    response.getProfile().getName(),
                    response.getAccessToken(),
                    response.getProfile().getId()
            ));*/

            Platform.runLater(() -> {

            });
        });
    }
}