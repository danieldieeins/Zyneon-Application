package live.nerotv.zyneon.app.frontend;

import javafx.application.Application;
import javafx.concurrent.Worker;
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import live.nerotv.zyneon.app.backend.login.MicrosoftAuth;
import netscape.javascript.JSObject;

public class WebViewApp extends Application {

    public Stage primaryStage;
    public WebView webView;
    public WebEngine webEngine;
    public Scene scene;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        webView = new WebView();
        webEngine = webView.getEngine();
        webEngine.setJavaScriptEnabled(true);
        webEngine.getLoadWorker().stateProperty().addListener((ov, oldState, newState) -> {
            if (newState == Worker.State.SUCCEEDED) {
                JSObject jsObject = (JSObject) webEngine.executeScript("window");
                jsObject.setMember("backendConnector", new BackendConnector());
            }
        });
        webEngine.load(getClass().getResource("/index.html").toExternalForm());

        scene = new Scene(webView, 1280, 800);
        primaryStage.setMinHeight(800);
        primaryStage.setMinWidth(1280);
        if(MicrosoftAuth.isUserSignedIn()) {
            primaryStage.setTitle("Zyneon App ("+MicrosoftAuth.getAuthInfos().getUsername()+")");
        } else {
            primaryStage.setTitle("Zyneon App (Alpha 0.0.9)");
        }
        primaryStage.setScene(scene);

        primaryStage.setOnCloseRequest(event -> {
            System.exit(0);
        });

        primaryStage.show();
    }

    public void start(String[] args) {
        launch(args);
    }
}
