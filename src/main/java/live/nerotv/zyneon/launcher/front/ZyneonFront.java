package live.nerotv.zyneon.launcher.front;

import live.nerotv.zyneon.launcher.front.backend.ZyneonFrontBackendSocket;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.net.URISyntaxException;

public class ZyneonFront {

    private JFrame mainFrame;
    private final ZyneonFrontBackendSocket backendSocket;

    public ZyneonFront() throws URISyntaxException, IOException {
        backendSocket = new ZyneonFrontBackendSocket();
    }

    public void open() {
        backendSocket.open();

        mainFrame = new JFrame();
        mainFrame.setUndecorated(true);
        mainFrame.setSize(1280, 720);
        mainFrame.setLocationRelativeTo(null);

        mainFrame.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE,0), "close_K");
        mainFrame.getRootPane().getActionMap().put("close_K", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                close();
                backendSocket.close();
                System.exit(1);
            }
        });

        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JEditorPane jEditorPane = new JEditorPane();



        try {
            jEditorPane.setContentType("text/html");

            jEditorPane.getDocument().putProperty("base", "http://localhost:" + backendSocket.getBackendWebPort() + "/");

            jEditorPane.setPage("http://localhost:" + backendSocket.getBackendWebPort() + "/web");

        } catch (IOException e) {
            e.printStackTrace();
            jEditorPane.setText("<!DOCTYPE html><html lang=\"de\"><head><meta charset=\"UTF-8\"><title>Fehler | Zyneon Launcher</title></head><body><p>Kann nicht geladen werden</p></body></html>");
        }

        jEditorPane.setEditable(false);

        JScrollPane scrollPane = new JScrollPane(jEditorPane);
        scrollPane.setWheelScrollingEnabled(false);
        mainFrame.add(scrollPane);

        mainFrame.setVisible(true);
    }

    public void close() {
        mainFrame.setVisible(false);

        backendSocket.close();
    }

}