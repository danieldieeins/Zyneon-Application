package com.zyneonstudios.application.backend.utils.frame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ComponentResizer extends MouseAdapter {
    private static final int SE = 8;
    private static final int SW = 9;
    private static final int NW = 7;
    private static final int NE = 6;
    private static final int E = 11;
    private static final int W = 10;
    private static final int N = 8;
    private static final int S = 9;

    private int cursor;
    private Point startPoint;
    private JFrame frame;

    public void registerComponent(JFrame frame) {
        frame.addMouseListener(this);
        frame.addMouseMotionListener(this);
        this.frame = frame;
    }

    public void mousePressed(MouseEvent e) {
        startPoint = e.getPoint();
        cursor = getCursor(e);
    }

    public void mouseDragged(MouseEvent e) {
        Point endPoint = e.getPoint();
        int x = frame.getX();
        int y = frame.getY();
        int width = frame.getWidth();
        int height = frame.getHeight();

        int deltaX = endPoint.x - startPoint.x;
        int deltaY = endPoint.y - startPoint.y;

        switch (cursor) {
            case NW:
                frame.setSize(width - deltaX, height - deltaY);
                frame.setLocation(x + deltaX, y + deltaY);
                break;
            case NE:
                frame.setSize(width + deltaX, height - deltaY);
                frame.setLocation(x, y + deltaY);
                break;
            case E:
                frame.setSize(width + deltaX, height);
                break;
            case W:
                frame.setSize(width - deltaX, height);
                frame.setLocation(x + deltaX, y);
                break;
            case N:
                frame.setSize(width, height - deltaY);
                frame.setLocation(x, y + deltaY);
                break;
            case S:
                frame.setSize(width, height + deltaY);
                break;
        }
        startPoint = endPoint;
    }

    public void mouseMoved(MouseEvent e) {
        int cursor = getCursor(e);
        if (cursor == SE || cursor == NW) {
            frame.setCursor(new Cursor(Cursor.SE_RESIZE_CURSOR));
        } else if (cursor == SW || cursor == NE) {
            frame.setCursor(new Cursor(Cursor.SW_RESIZE_CURSOR));
        } else if (cursor == E || cursor == W) {
            frame.setCursor(new Cursor(Cursor.E_RESIZE_CURSOR));
        } else {
            frame.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        }
    }

    private int getCursor(MouseEvent e) {
        Point p = e.getPoint();
        int x = (int) p.getX();
        int y = (int) p.getY();
        int width = frame.getWidth();
        int height = frame.getHeight();

        if (x < SE && y < SE) {
            return SW;
        } else if (x < SE && y > height - SE) {
            return NW;
        } else if (x > width - SE && y > height - SE) {
            return NE;
        } else if (x > width - SE && y < SE) {
            return SE;
        } else if (x > width - E) {
            return E;
        } else if (x < W) {
            return W;
        } else if (y < N) {
            return N;
        } else if (y > height - S) {
            return S;
        }
        return -1;
    }
}