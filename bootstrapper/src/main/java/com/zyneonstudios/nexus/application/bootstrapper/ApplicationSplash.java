package com.zyneonstudios.nexus.application.bootstrapper;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.Objects;

public class ApplicationSplash extends JWindow {

    public ApplicationSplash(String path) {
        super();
        try {
            setBackground(new Color(0, 0, 0, 0));
            setSize(400, 400);
            setLocationRelativeTo(null);
            JLabel image = new JLabel(new ImageIcon(ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream(path))).getScaledInstance(getWidth(), getHeight(), Image.SCALE_SMOOTH)));
            getContentPane().add(image);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}