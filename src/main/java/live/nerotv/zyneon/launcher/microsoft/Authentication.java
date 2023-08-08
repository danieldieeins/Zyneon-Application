package live.nerotv.zyneon.launcher.microsoft;

import com.sun.net.httpserver.HttpServer;

import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Random;
import java.util.stream.Collectors;

public class Authentication {


    public static void main(String[] args) {
        System.out.println(new Authentication().startAuth());
    }

    private HttpServer socket;
    private int port;

    public Authentication() {
        Random random = new Random();

        port = 20003;

        if(available(port)) {
            throw new RuntimeException("port is not available");
        }

        try {
            socket = HttpServer.create(new InetSocketAddress("localhost", port), 0);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        socket.createContext("/auth", exchange -> {
            String response = "<!DOCTYPE html>\n" +
                    "<html>\n" +
                    "<head>\n" +
                    "    <title>OAuth Callback</title>\n" +
                    "</head>\n" +
                    "<body>\n" +
                    "    <script>\n" +
                    "        // Extract access token from the URL fragment\n" +
                    "        const fragment = window.location.hash.substring(1);\n" +
                    "        const keyValuePairs = fragment.split('&');\n" +
                    "        const tokenInfo = {};\n" +
                    "        for (const pair of keyValuePairs) {\n" +
                    "            const [key, value] = pair.split('=');\n" +
                    "            tokenInfo[key] = value;\n" +
                    "        }\n" +
                    "        \n" +
                    "        // Send the tokenInfo back to the server (you can use AJAX or other methods)\n" +
                    "        const xhr = new XMLHttpRequest();\n" +
                    "        xhr.open('POST', 'http://localhost:20003/token', true);\n" +
                    "        xhr.setRequestHeader('Content-Type', 'application/json');\n" +
                    "        xhr.onreadystatechange = function() {\n" +
                    "            if (xhr.readyState === XMLHttpRequest.DONE && xhr.status === 200) {\n" +
                    "                console.log('Token sent to server.');\n" +
                    "            }\n" +
                    "        };\n" +
                    "        xhr.send(JSON.stringify(tokenInfo));\n" +
                    "    </script>\n" +
                    "</body>\n" +
                    "</html>";
            exchange.sendResponseHeaders(200, response.getBytes(StandardCharsets.UTF_8).length);
            exchange.getResponseBody().write(response.getBytes());
            exchange.getResponseHeaders().set("Content-Type", "text/html");
            exchange.getResponseBody().flush();
            exchange.getResponseBody().close();
        });

        socket.createContext("/token", exchange -> {
            InputStream requestBody = exchange.getRequestBody();
            BufferedReader reader = new BufferedReader(new InputStreamReader(requestBody));
            String jsonData = reader.lines().collect(Collectors.joining());

            // Process the JSON data as needed
            System.out.println("Received JSON data: " + jsonData);

            // Set the response content type
            exchange.getResponseHeaders().set("Content-Type", "text/html");

            // Send a response
            String response = "<html><head><title>Successful</title><script>window.close();</script></head><body>JSON data received and processed. Closing window...</body></html>";
            exchange.sendResponseHeaders(200, response.length());
            exchange.getResponseBody().write(response.getBytes());
            exchange.close();
        });
    }

    boolean completed;
    String info;

    public String startAuth() {
        socket.start();

        String clientId = "5193b2fd-a1d1-40f0-a0e2-2e982688f7db";
        String redirectUri = "http://localhost:" + port + "/auth";
        String scope = "openid profile email offline_access";

        String authUrl = "https://login.microsoftonline.com/common/oauth2/v2.0/authorize" +
                "?client_id=" + clientId +
                "&response_type=token" +
                "&redirect_uri=" + URLEncoder.encode(redirectUri) +
                "&scope=" + URLEncoder.encode(scope);

        try {
            Desktop.getDesktop().browse(new URI(authUrl));
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException(e);
        }

        // Wait for the callback to complete
        while (!completed) {
            try {
                Thread.sleep(100); // Sleep to avoid busy-waiting
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        socket.stop(0);
        return info;
    }


    private boolean available(int port) throws IllegalStateException {
        try (Socket ignored = new Socket("localhost", port)) {
            return true;
        } catch (ConnectException e) {
            return false;
        } catch (IOException e) {
            throw new IllegalStateException("Error while trying to check open port", e);
        }
    }

}
