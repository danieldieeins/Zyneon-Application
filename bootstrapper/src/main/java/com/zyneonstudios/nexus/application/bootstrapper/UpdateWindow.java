package com.zyneonstudios.nexus.application.bootstrapper;

import javax.swing.*;
import java.awt.*;

public class UpdateWindow extends JFrame {

    private final JPanel main;

    public UpdateWindow() {
        setUndecorated(true);
        setSize(new Dimension(400,150));
        setLocationRelativeTo(null);
        main = mainPanel();
        add(main);
        main.add(progress());
    }

    private JPanel mainPanel() {
        JPanel panel = new JPanel();
        panel.setSize(getSize());
        panel.setLayout(new BoxLayout(panel,BoxLayout.Y_AXIS));
        panel.setBackground(Color.decode("#1a1b1c"));
        return panel;
    }

    private JPanel progress() {
        JPanel panel = new JPanel();
        panel.setSize(new Dimension(390,30));
        panel.setBackground(Color.decode("#8142ed"));
        return panel;
    }
}