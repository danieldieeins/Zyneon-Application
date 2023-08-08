package live.nerotv.zyneon.launcher.frontend;

import javafx.application.Application;
import javafx.concurrent.Worker;
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import netscape.javascript.JSObject;

public class WebViewApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        WebView webView = new WebView();
        WebEngine webEngine = webView.getEngine();
        webEngine.setJavaScriptEnabled(true);
        webEngine.getLoadWorker().stateProperty().addListener((ov, oldState, newState) -> {
            if (newState == Worker.State.SUCCEEDED) {
                JSObject jsObject = (JSObject) webEngine.executeScript("window");
                jsObject.setMember("backendConnector", new BackendConnector());
            }
        });
        webEngine.load(getClass().getResource("/index.html").toExternalForm());

        Scene scene = new Scene(webView, 1280, 800);
        primaryStage.setMinHeight(800);
        primaryStage.setMinWidth(1280);
        primaryStage.setTitle("Zyneon Launcher");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public void start(String[] args) {
        launch(args);
    }
}
