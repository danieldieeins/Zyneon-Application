package live.nerotv.zyneon.app.application.backend.utils;

import live.nerotv.zyneon.app.application.Application;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;

public class HTTPServer {

    final int port;

    public HTTPServer(int port) {
        this.port = port;
    }

    public void run() {
        String newLine = "\r\n";
        try {
            ServerSocket socket = new ServerSocket(port);
            while (true) {
                Socket connection = socket.accept();
                try {
                    BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    OutputStream out = new BufferedOutputStream(connection.getOutputStream());
                    PrintStream pout = new PrintStream(out);
                    String request = in.readLine();
                    if (request == null) continue;
                    while (true) {
                        String ignore = in.readLine();
                        if (ignore == null || ignore.length() == 0) break;
                    }
                    if (!request.startsWith("GET ") || !(request.endsWith(" HTTP/1.0") || request.endsWith(" HTTP/1.1"))) {
                        pout.print("HTTP/1.0 400 Bad Request" + newLine + newLine);
                    } else {
                        String response;
                        try {
                            response = readFileContent(Application.instances.getPath());
                            // Hinzufügen der CORS-Header
                            pout.print("HTTP/1.0 200 OK" + newLine +
                                    "Content-Type: application/json" + newLine +
                                    "Date: " + new Date() + newLine +
                                    "Content-length: " + response.length() + newLine +
                                    "Access-Control-Allow-Origin: *" + newLine +
                                    newLine + response);
                        } catch (IOException e) {
                            pout.print("HTTP/1.0 500 Internal Server Error" + newLine + newLine);
                        }
                    }
                    pout.close();
                } catch (Throwable tri) {
                    System.err.println("Error handling request: " + tri);
                }
            }
        } catch (Throwable tr) {
            System.err.println("Could not start server: " + tr);
        }
    }

    private static String readFileContent(String filePath) throws IOException {
        Path path = Paths.get(filePath);
        byte[] fileContent = Files.readAllBytes(path);
        return new String(fileContent);
    }
}
