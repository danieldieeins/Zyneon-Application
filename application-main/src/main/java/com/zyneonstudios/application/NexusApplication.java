package com.zyneonstudios.application;

import com.formdev.flatlaf.FlatDarkLaf;
import com.zyneonstudios.Main;
import com.zyneonstudios.application.frame.ApplicationFrame;
import live.nerotv.shademebaby.utils.FileUtil;

import javax.swing.*;
import java.io.File;

import static com.zyneonstudios.Main.getApplicationPath;

public class NexusApplication{

    private final ApplicationFrame frame;

    public NexusApplication(String[] args) {
        update();
        try {
            FlatDarkLaf.setup();
            UIManager.setLookAndFeel(new FlatDarkLaf());
        } catch (Exception ignore) {}
        frame = new ApplicationFrame(getApplicationPath()+"temp/ui/start.html", getApplicationPath()+"libs/jcef/");
    }

    private static boolean update() {
        boolean updated;
        try {
            if(new File(getApplicationPath() + "temp/ui/").exists()) {
                new File(getApplicationPath() + "temp/ui/").delete();
            }
            new File(getApplicationPath() + "temp/ui/").mkdirs();
            FileUtil.extractResourceFile("ui.zip",getApplicationPath()+"temp/ui.zip",Main.class);
            FileUtil.unzipFile(getApplicationPath()+"temp/ui.zip", getApplicationPath() + "temp/ui");
            new File(getApplicationPath()+"temp/ui.zip").delete();
            updated = true;
        } catch (Exception e) {
            System.err.println("Couldn't update application user interface: "+e.getMessage());
            updated = false;
        }
        new File(getApplicationPath() + "updater.json").delete();
        new File(getApplicationPath() + "version.json").delete();
        return updated;
    }

    public void launch() {
        frame.setVisible(true);
    }
}