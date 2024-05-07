package com.zyneonstudios.application.frame.web;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class CustomApplicationFrame extends ApplicationFrame {

    /*
     * Zyneon Application custom application frame
     * by nerotvlive
     * */

    private static Point mouseDownCompCoords; // Stores mouse coordinates for frame dragging
    private String title; // Title of the frame
    private JPanel titleBar; // Panel for custom title bar
    private boolean border; // Flag to track frame border state

    // Constructor
    public CustomApplicationFrame(String url, String jcefPath) {
        super(url,jcefPath); // Call superclass constructor
        setUndecorated(true); // Remove default window decorations
        title = "  Zyneon Application"; // Set default title
        JPanel customTitleBar = createCustomTitleBar(); // Create custom title bar
        getContentPane().add(customTitleBar, BorderLayout.NORTH); // Add custom title bar to content pane
        addDragAndDropFunctionality(); // Add drag-and-drop functionality
        setBorder(true); // Set default border state
        setMinimumSize(new Dimension(1150,700)); // Set minimum size for the frame
    }

    // Method to set the title bar properties
    @Override
    public void setTitlebar(String title, Color background, Color foreground) {
        setTitle(title); // Set frame title
        setTitleBackground(background); // Set title bar background color
        setTitleForeground(foreground); // Set title bar foreground color
    }

    // Setter method for the title
    @Override
    public void setTitle(String title) {
        super.setTitle(title); // Call superclass method
        this.title = title; // Update title
    }

    // Setter method for the title bar background color
    @Override
    public void setTitleBackground(Color color) {
        setBackground(color); // Set frame background color
        titleBar.setBackground(color); // Set title bar background color
    }

    // Setter method for the title bar foreground color
    @Override
    public void setTitleForeground(Color color) {
        titleBar.setForeground(color); // Set title bar foreground color
    }

    // Method to create custom title bar
    private JPanel createCustomTitleBar() {
        titleBar = new JPanel(new BorderLayout()); // Create title bar panel
        titleBar.setBackground(Color.BLACK); // Set default background color
        titleBar.setForeground(Color.WHITE); // Set default foreground color

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0)); // Panel for buttons
        buttonPanel.setBackground(null); // Set background color to null
        buttonPanel.setForeground(null); // Set foreground color to null

        // Minimize button
        JButton minimizeButton = new JButton("-");
        minimizeButton.setPreferredSize(new Dimension(30,30));
        minimizeButton.addActionListener(e -> setState(ICONIFIED)); // Minimize the frame
        minimizeButton.setBorder(null);
        minimizeButton.setBackground(null);
        minimizeButton.setForeground(null);
        buttonPanel.add(minimizeButton);

        // Maximize button
        JButton maximizeButton = new JButton("◻");
        maximizeButton.setPreferredSize(new Dimension(30,30));
        maximizeButton.setBorder(null);
        maximizeButton.setBackground(null);
        maximizeButton.setForeground(null);
        maximizeButton.addActionListener(e -> toggleMaximizeState()); // Toggle maximize state
        buttonPanel.add(maximizeButton);

        // Close button
        JButton closeButton = new JButton("X");
        closeButton.setPreferredSize(new Dimension(30,30));
        closeButton.setBorder(null);
        closeButton.setBackground(null);
        closeButton.setForeground(null);
        closeButton.addMouseListener(new MouseAdapter() {
            // Change background color on mouse hover
            public void mouseEntered(MouseEvent evt) {
                closeButton.setBackground(Color.RED);
            }

            public void mouseExited(MouseEvent evt) {
                closeButton.setBackground(null); // Reset background color
            }
        });
        closeButton.addActionListener(e -> System.exit(0)); // Exit application
        buttonPanel.add(closeButton);

        JLabel titleLabel = new JLabel(title); // Label for title
        titleLabel.setBackground(null);
        titleLabel.setForeground(null);

        titleBar.add(buttonPanel, BorderLayout.EAST); // Add button panel to title bar
        titleBar.add(titleLabel, BorderLayout.CENTER); // Add title label to title bar

        return titleBar; // Return the custom title bar
    }

    // Method to toggle maximize state of the frame
    private void toggleMaximizeState() {
        if ((getExtendedState() & MAXIMIZED_BOTH) == 0) {
            setExtendedState(getExtendedState() | MAXIMIZED_BOTH); // Maximize the frame
        } else {
            setExtendedState(getExtendedState() & ~MAXIMIZED_BOTH); // Restore normal state
        }
        toggleBorder(); // Toggle frame border
    }

    // Method to add drag-and-drop functionality to the frame
    private void addDragAndDropFunctionality() {
        // Mouse listener for mouse press event
        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                if ((getExtendedState() & MAXIMIZED_BOTH) == 0) {
                    if (isWithinTitleBar(e)) {
                        mouseDownCompCoords = e.getPoint(); // Store mouse coordinates
                    }
                } else {
                    setExtendedState(NORMAL); // Restore normal state
                    setBorder(true); // Set frame border
                    mouseDownCompCoords = e.getPoint(); // Store mouse coordinates
                }
            }
            // Check if mouse event is within title bar
            private boolean isWithinTitleBar(MouseEvent e) {
                return (e.getY() < titleBar.getHeight() && e.getX() > 0 && e.getX() < getWidth());
            }
        });
        // Mouse motion listener for drag events
        addMouseMotionListener(new MouseAdapter() {
            public void mouseDragged(MouseEvent e) {
                if (isWithinTitleBar(e)) {
                    Point currCoords = e.getLocationOnScreen();
                    setLocation(currCoords.x - mouseDownCompCoords.x, currCoords.y - mouseDownCompCoords.y); // Move frame
                }
            }
            // Check if mouse event is within title bar
            private boolean isWithinTitleBar(MouseEvent e) {
                return (e.getY() < titleBar.getHeight() && e.getX() > 0 && e.getX() < getWidth());
            }
        });
    }

    // Method to toggle frame border
    private void toggleBorder() {
        setBorder(!border); // Toggle border state
    }

    // Method to set frame border
    private void setBorder(boolean state) {
        if (state) {
            getRootPane().setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY)); // Set frame border
        } else {
            getRootPane().setBorder(null); // Remove frame border
        }
        border = state; // Update border state
    }
}
