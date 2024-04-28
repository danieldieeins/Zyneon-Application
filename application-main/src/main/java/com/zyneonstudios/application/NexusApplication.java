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
    private String urlBase = null;

    public NexusApplication(String[] args) {
        resolveArgs(args); update();
        try {
            FlatDarkLaf.setup();
            UIManager.setLookAndFeel(new FlatDarkLaf());
        } catch (Exception ignore) {}
        if(urlBase==null) {
            urlBase = "file://"+getApplicationPath()+"temp/ui/";
        }
        frame = new ApplicationFrame(urlBase+"start.html", getApplicationPath()+"libs/jcef/");
    }

    private void resolveArgs(String[] args) {
        for(String arg:args) {
            if(arg.startsWith("--ui:")) {
                urlBase = arg.replace("--ui:", "");
            }
        }
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