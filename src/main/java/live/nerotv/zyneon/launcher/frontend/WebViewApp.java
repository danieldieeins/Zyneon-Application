package live.nerotv.zyneon.launcher.frontend;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

public class WebViewApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        WebView webView = new WebView();
        webView.getEngine().load(getClass().getResource("/index.html").toExternalForm());

        Scene scene = new Scene(webView, 1280, 801);
        primaryStage.setMinHeight(801);
        primaryStage.setMinWidth(1280);

        primaryStage.setTitle("Zyneon Launcher");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}