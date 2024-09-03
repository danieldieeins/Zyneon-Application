package com.zyneonstudios.application.frame;

import com.zyneonstudios.nexus.desktop.frame.NexusFrame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

public class ApplicationFrame extends NexusFrame implements ComponentListener {

    private JPanel menu = null;
    private JPanel content = null;

    public ApplicationFrame() {
        super("NEXUS App",false);
        getContentPane().setBackground(Color.black);
        setTitleColors(Color.black,Color.white);
        getContent().setLayout(new BorderLayout());
        addComponentListener(this);
        initMenu();
        initContent();
    }

    private void initMenu() {
        menu = new JPanel();
        menu.setPreferredSize(new Dimension(288, 0));
        menu.setBackground(Color.black);
        getContentPane().add(menu, BorderLayout.WEST);
    }

    private void initContent() {
        content = new JPanel();
        content.setBackground(Color.white);
        content.setPreferredSize(new Dimension((getWidth()-288), 0));
        getContentPane().add(content,BorderLayout.EAST);
    }

    public JPanel getMenu() {
        if(menu == null) {
            initMenu();
        }
        return menu;
    }

    public JPanel getContent() {
        if(content == null) {
            initContent();
        }
        return content;
    }

    @Override
    public void componentResized(ComponentEvent e) {
        menu.setPreferredSize(new Dimension(288, 0));
        content.setPreferredSize(new Dimension((getWidth()-288), 0));
    }

    @Override
    public void componentMoved(ComponentEvent e) {
        menu.setPreferredSize(new Dimension(288, 0));
        content.setPreferredSize(new Dimension((getWidth()-288), 0));
    }

    @Override
    public void componentShown(ComponentEvent e) {
        menu.setPreferredSize(new Dimension(288, 0));
        content.setPreferredSize(new Dimension((getWidth()-288), 0));
    }

    @Override
    public void componentHidden(ComponentEvent e) {
        menu.setPreferredSize(new Dimension(288, 0));
        content.setPreferredSize(new Dimension((getWidth()-288), 0));
    }
}