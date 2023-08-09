package live.nerotv.zyneon.app.backend.login;

import fr.litarvan.openauth.microsoft.MicrosoftAuthResult;
import fr.litarvan.openauth.microsoft.MicrosoftAuthenticationException;
import fr.litarvan.openauth.microsoft.MicrosoftAuthenticator;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import live.nerotv.Main;

import java.util.Base64;
import java.util.UUID;

public class MicrosoftAuth {

    private static AuthInfos authInfos;

    public static boolean isUserSignedIn() {
        try {
            if (Main.config.get("account.access") != null && Main.config.get("account.refresh") != null) {
                try {
                    MicrosoftAuthenticator authenticator = new MicrosoftAuthenticator();
                    MicrosoftAuthResult response = authenticator.loginWithRefreshToken(new String(Base64.getDecoder().decode((String)Main.config.get("account.refresh"))));

                    Main.config.set("account.access", response.getAccessToken());
                    Main.config.set("account.refresh", response.getRefreshToken());
                    authInfos = new AuthInfos(response.getProfile().getName(), response.getAccessToken(), response.getProfile().getId());
                    return true;
                } catch (MicrosoftAuthenticationException e) {
                    Main.config.delete("account.access");
                    Main.config.delete("account.refresh");
                }
            } else if (Main.config.get("account.offline") != null) {
                authInfos = new AuthInfos((String) Main.config.get("account.offline"), UUID.randomUUID().toString(), UUID.randomUUID().toString());
                return true;
            }
            return false;
        } catch (RuntimeException e) {
            Main.config.delete("account.access");
            Main.config.delete("account.refresh");
            return false;
        }
    }

    public static AuthInfos getAuthInfos() {
        return authInfos;
    }

    private static boolean isOpen = false;

    public static void authenticateMS() {
        if(!isOpen) {
            isOpen = true;
            MicrosoftAuthenticator authenticator = new MicrosoftAuthenticator();
            authenticator.loginWithAsyncWebview().whenComplete((response, error) -> {
                if (error != null) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("error");
                    alert.setContentText(error.getMessage());
                    alert.show();
                    Platform.runLater(() -> isOpen = false);
                    return;
                }
                Main.config.set("account.access", Base64.getEncoder().encodeToString(response.getAccessToken().getBytes()));
                Main.config.set("account.refresh", Base64.getEncoder().encodeToString(response.getRefreshToken().getBytes()));
                Main.config.set("account.name", Base64.getEncoder().encodeToString(response.getProfile().getName().getBytes()));
                Main.config.set("account.id", Base64.getEncoder().encodeToString(response.getProfile().getId().getBytes()));
                authInfos = new AuthInfos(response.getProfile().getName(),response.getAccessToken(),response.getProfile().getId());
                Platform.runLater(() -> isOpen = false);
            });
        }
    }
}