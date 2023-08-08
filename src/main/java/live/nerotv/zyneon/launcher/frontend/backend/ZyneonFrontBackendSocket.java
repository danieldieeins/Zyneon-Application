package live.nerotv.zyneon.launcher.frontend.backend;

import com.sun.net.httpserver.HttpServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Random;
import java.util.Scanner;

public class ZyneonFrontBackendSocket {

    private HttpServer socket;
    private int port;

    public ZyneonFrontBackendSocket() {

        Random random = new Random();

        port = random.nextInt(2000,20000);

        if(!available(port)) {
            while (!available(port)) {
                port = random.nextInt(2000,20000);
            }
        }

        try {
            socket = HttpServer.create(new InetSocketAddress("localhost", port), 0);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        socket.createContext("/web", exchange -> {

            System.out.println(exchange.getRequestURI().toString());

            Scanner webPage = new Scanner(ZyneonFrontBackendSocket.class.getClassLoader().getResourceAsStream("index.html"), StandardCharsets.UTF_8);

            String page = "";

            while (webPage.hasNext()) {
                page = page + webPage.next() + "\n";
            }

            exchange.sendResponseHeaders(200, page.getBytes().length);
            exchange.getResponseBody().write(page.getBytes(StandardCharsets.UTF_8));
            exchange.getResponseBody().flush();

            exchange.getResponseBody().close();
            exchange.close();
        });

        socket.createContext("/assets", exchange -> {

            System.out.println(exchange.getRequestURI().toString());

            String requestedAsset = exchange.getRequestURI().getPath();  // /assets/styles.css
            String assetContent = readAssetContent(requestedAsset);

            // Set appropriate content type
            exchange.getResponseHeaders().set("Content-Type", getContentType(requestedAsset));

            exchange.sendResponseHeaders(200, assetContent.getBytes().length);
            exchange.getResponseBody().write(assetContent.getBytes(StandardCharsets.UTF_8));
            exchange.getResponseBody().flush();

            exchange.getResponseBody().close();
            exchange.close();
        });
    }

    private String getContentType(String assetPath) {
        if (assetPath.endsWith(".css")) {
            return "stylesheet";
        } else if (assetPath.endsWith(".js")) {
            return "text/javascript";
        } else if (assetPath.endsWith(".jpg") || assetPath.endsWith(".jpeg")) {
            return "img/jpeg";
        } else if (assetPath.endsWith(".png")) {
            return "img/png";
        }

        // Default content type for unknown assets
        return "application/octet-stream";
    }

    private String readAssetContent(String assetPath) throws IOException {
        // Assuming assets are in the same directory as your server class
        InputStream inputStream = ZyneonFrontBackendSocket.class.getResourceAsStream(assetPath);

        if (inputStream == null) {
            throw new IOException("Asset not found: " + assetPath);
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            StringBuilder content = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }

            return content.toString();
        }
    }


    public void open() {
        socket.start();
    }

    public int getBackendWebPort() {
        return port;
    }

    public void close() {
        socket.stop(0);
    }

    private boolean available(int port) throws IllegalStateException {
        try (Socket ignored = new Socket("127.0.0.1", port)) {
            return false;
        } catch (ConnectException e) {
            return true;
        } catch (IOException e) {
            throw new IllegalStateException("Error while trying to check open port", e);
        }
    }

}
