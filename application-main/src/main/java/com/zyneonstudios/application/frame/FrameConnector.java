package com.zyneonstudios.application.frame;

import java.awt.*;

public class FrameConnector {

    private final ApplicationFrame frame;

    public FrameConnector(ApplicationFrame frame) {
        this.frame = frame;
    }

    public void resolveRequest(String request) {
        System.out.println(" ");
        System.out.println("[CONNECTOR] resolving "+request+"...");
        if(request.startsWith("sync.")) {
            sync(request.replace("sync.",""));
            System.out.println("[CONNECTOR] successfully resolved "+request);
        } else {
            System.err.println("[CONNECTOR] couldn't resolve "+request+".");
        }

    }

    public void sync(String request) {
        frame.executeJavaScript("syncDesktop();");
        if(request.startsWith("title.")) {
            String[] request_ = request.replace("title.","").split("-.-",2);
            Color background;
            Color foreground;
            if(request_[0].equalsIgnoreCase("assets/css/app-colors-dark.css")) {
                background = Color.black;
                foreground = Color.white;
            } else {
                background = Color.white;
                foreground = Color.black;
            }
            String title = request_[1];
            frame.setTitle(title,background,foreground);
        }
    }
}