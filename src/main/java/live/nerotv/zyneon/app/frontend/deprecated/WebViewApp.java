package live.nerotv.zyneon.app.frontend.deprecated;

import javafx.application.Application;
import javafx.concurrent.Worker;
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import live.nerotv.zyneon.app.backend.login.MicrosoftAuth;
import netscape.javascript.JSObject;

@Deprecated
public class WebViewApp extends Application {

    @Deprecated
    public Stage primaryStage;

    @Deprecated
    public WebView webView;

    @Deprecated
    public WebEngine webEngine;

    @Deprecated
    public Scene scene;

    @Override
    @Deprecated
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
        webEngine.setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/114.0.0.0 Safari/537.36 OPR/100.0.0.0");
        webEngine.load("https://a.nerotv.live/zyneon/launcher/html2/index.html");
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

    @Deprecated
    public void start(String[] args) {
        launch(args);
    }
}
