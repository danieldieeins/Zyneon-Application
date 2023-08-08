package live.nerotv.zyneon.launcher.frontend.backend;

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.Socket;
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
            Scanner webPage = new Scanner(ZyneonFrontBackendSocket.class.getClassLoader().getResourceAsStream("index.html"), StandardCharsets.UTF_8);

            String page = "";

            while (webPage.hasNext()) {
                page = page + webPage.next() + "\n";
            }

            System.out.println(page);

            exchange.sendResponseHeaders(200, page.getBytes().length);
            exchange.getResponseBody().write(page.getBytes(StandardCharsets.UTF_8));
            exchange.getResponseBody().flush();

            exchange.getResponseBody().close();
            exchange.close();
        });
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