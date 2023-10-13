package com.zyneonstudios.application;// Import-Anweisung für unseren JFrame
import javax.swing.JFrame;
// Import-Anweisung für unser JLabel
import javax.swing.JLabel;
// Import-Anweisung für unsere Farben
import java.awt.Color;

public class ZyneonApplication {
    public static void main (String[] args) {
        // Erzeugung eines neuen Frames mit dem Titel "Mein JFrame Beispiel"
        JFrame meinFrame = new JFrame("Mein JFrame Beispiel");
        // Wir setzen die Breite und die Höhe unseres Fensters auf 200 Pixel
        meinFrame.setSize(200, 200);
        // Wir setzen die Hintergrundfarbe des Fensters auf Schwarz
        meinFrame.setBackground(Color.BLACK);
        // Wir setzen die Vordergrundfarbe des Fensters auf Weiß
        meinFrame.setForeground(Color.WHITE);
        // Hinzufügen einer einfachen Komponente (hier: JLabel)
        meinFrame.add(new JLabel("Beispiel JLabel"));
        // Wir lassen unseren Frame anzeigen
        meinFrame.setVisible(true);
    }
}
