package com.zyneonstudios.application.backend.utils.frame;

import com.sun.management.OperatingSystemMXBean;
import live.nerotv.shademebaby.file.Config;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.lang.management.ManagementFactory;

public class MemoryFrame extends JFrame {

    private final Config saveFile;
    private final String title;
    private String instance;

    public MemoryFrame(Config saveFile, String title, String instance) {
        this.saveFile = saveFile;
        this.title = title;
        this.instance = instance;
        SwingUtilities.invokeLater(this::init);
    }

    private void init() {
        if(instance!=null) {
            instance = instance.replace("/","").replace(".","");
        }
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                dispose();
            }
        });
        setLayout(new FlowLayout());

        OperatingSystemMXBean os = (OperatingSystemMXBean)ManagementFactory.getOperatingSystemMXBean();
        long c = 1024L*1024L;
        long maxValue = os.getTotalMemorySize() /c;

        int minValue = 0;
        int initialValue = 0;
        if(saveFile.get("settings.memory.default")!=null) {
            initialValue = saveFile.getInteger("settings.memory.default");
        }
        if(instance!=null) {
            if(saveFile.get("settings.memory."+instance)!=null) {
                initialValue = saveFile.getInteger("settings.memory."+instance);
            }
        }
        JSlider slider = new JSlider(minValue, (int)maxValue, initialValue);
        double alleWieViel = (double)maxValue/2;
        slider.setMajorTickSpacing((int)alleWieViel);
        slider.setMinorTickSpacing((int)alleWieViel/2);
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

        JTextField textField = new JTextField(10);
        textField.setText(initialValue+"");
        JLabel label = new JLabel("MB");
        slider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                textField.setText(String.valueOf(slider.getValue()));
                if(instance==null) {
                    saveFile.set("settings.memory.default", slider.getValue());
                } else {
                    if(instance.equalsIgnoreCase("")) {
                        instance = "default";
                    }
                    saveFile.set("settings.memory."+instance, slider.getValue());
                }
            }
        });

        textField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int value = Integer.parseInt(textField.getText());
                    if (value >= minValue && value <= maxValue) {
                        slider.setValue(value);
                    } else {
                        textField.setText(String.valueOf(slider.getValue()));
                    }
                } catch (NumberFormatException ex) {
                    textField.setText(String.valueOf(slider.getValue()));
                }
                if(instance==null) {
                    saveFile.set("settings.memory.default", slider.getValue());
                } else {
                    if(instance.equalsIgnoreCase("")) {
                        instance = "default";
                    }
                    saveFile.set("settings.memory."+instance, slider.getValue());
                }
            }
        });
        Dimension d = new Dimension(380,92);
        setMinimumSize(d);
        setSize(d);
        setResizable(false);
        setLocationRelativeTo(null);
        add(slider);
        add(textField);
        add(label);
        pack();
        setTitle(title);
        setVisible(true);
    }
}