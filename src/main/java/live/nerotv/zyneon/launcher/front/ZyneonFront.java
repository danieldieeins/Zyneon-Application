package live.nerotv.zyneon.launcher.front;

import live.nerotv.zyneon.launcher.front.backend.ZyneonFrontBackendSocket;

import javax.swing.*;
import java.io.IOException;
import java.net.URISyntaxException;

public class ZyneonFront {

    private JFrame mainFrame;
    private ZyneonFrontBackendSocket backendSocket;

    public ZyneonFront() throws URISyntaxException, IOException {
        backendSocket = new ZyneonFrontBackendSocket();

        mainFrame = new JFrame();
        mainFrame.setSize(60, 60);
    }

    public void open() {
        backendSocket.open();

        JEditorPane jEditorPane = new JEditorPane();

        try {
            jEditorPane.setPage("http://127.0.0.1:" + backendSocket.getBackendWebPort() + "/web");
        } catch (IOException e) {
            jEditorPane.setContentType("text/html");
            jEditorPane.setText("<!DOCTYPE html><html lang=\"de\"><head><meta charset=\"UTF-8\"><title>Fehler | Zyneon Launcher</title></head><body><p>Kann nicht geladen werden</p></body></html>");
        }

        JScrollPane scrollPane = new JScrollPane(jEditorPane);
        mainFrame.getContentPane().add(scrollPane);

        mainFrame.setVisible(true);
    }

    public void close() {
        mainFrame.setVisible(false);
        mainFrame.dispose();

        backendSocket.close();
    }

}