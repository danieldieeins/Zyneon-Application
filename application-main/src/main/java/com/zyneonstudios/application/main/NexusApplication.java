package com.zyneonstudios.application.main;

import com.formdev.flatlaf.FlatDarkLaf;
import com.zyneonstudios.Main;
import com.zyneonstudios.application.frame.web.ApplicationFrame;
import com.zyneonstudios.application.frame.web.CustomApplicationFrame;
import com.zyneonstudios.application.modules.ModuleLoader;
import com.zyneonstudios.application.modules.test.TestModule;
import live.nerotv.shademebaby.logger.Logger;
import live.nerotv.shademebaby.utils.FileUtil;

import javax.swing.*;
import java.awt.*;
import java.io.File;

import static com.zyneonstudios.application.main.ApplicationConfig.getApplicationPath;

public class NexusApplication{

    /*
     * Zyneon Application "main" object
     * by nerotvlive
     * Contributions are welcome. Please add your name to the "by" line if you make any modifications.
     * */

    private final JFrame frame;
    private static final Logger logger = new Logger("APP");
    private ModuleLoader moduleLoader = new ModuleLoader(this);

    public NexusApplication() {
        // Initializing the application frame
        logger.log("[APP] Updated application ui: "+update());
        try {
            FlatDarkLaf.setup();
            UIManager.setLookAndFeel(new FlatDarkLaf());
        } catch (Exception ignore) {}
        if(ApplicationConfig.getOS().startsWith("macOS")||ApplicationConfig.getOS().startsWith("Windows")) {
            // Creating a standard application frame for macOS and Windows
            frame = new ApplicationFrame(this, ApplicationConfig.urlBase + "start.html", getApplicationPath() + "libs/jcef/");
            frame.pack(); frame.setSize(new Dimension(1200,720));
        } else {
            // Creating a custom application frame for other operating systems
            frame = new CustomApplicationFrame(this, ApplicationConfig.urlBase + "start.html", getApplicationPath() + "libs/jcef/");
            frame.pack();
        }
        frame.setLocationRelativeTo(null);
        if(ApplicationConfig.test) {
            moduleLoader.loadModule(new TestModule(this));
        }

        File modules = new File(getApplicationPath()+"modules/");
        if(modules.exists()) {
            if(modules.isDirectory()) {
                try {
                    for(File module : modules.listFiles()) {
                        if(!module.isDirectory()) {
                            try {
                                moduleLoader.loadModule(moduleLoader.readModule(module));
                            } catch (Exception e) {
                                getLogger().debug("[APP] Cant read module "+module.getName()+": "+e.getMessage());
                            }
                        }
                    }
                } catch (Exception e) {
                    getLogger().error("[APP] Can't read modules: "+e.getMessage());
                }
            }
        }
    }

    public static Logger getLogger() {
        return logger;
    }

    public ModuleLoader getModuleLoader() {
        return moduleLoader;
    }

    public JFrame getFrame() {
        return frame;
    }

    // Method to update the application UI
    private static boolean update() {
        boolean updated;
        try {
            if(new File(getApplicationPath() + "temp/ui/").exists()) {
                logger.debug("[APP] Deleted old ui files: "+new File(getApplicationPath() + "temp/ui/").delete());
            }
            logger.debug("[APP] Created new ui path: "+new File(getApplicationPath() + "temp/ui/").mkdirs());
            FileUtil.extractResourceFile("content.zip",getApplicationPath()+"temp/content.zip",Main.class);
            FileUtil.unzipFile(getApplicationPath()+"temp/content.zip", getApplicationPath() + "temp/ui");
            logger.debug("[APP] Deleted ui archive: "+new File(getApplicationPath()+"temp/content.zip").delete());
            updated = true;
        } catch (Exception e) {
            logger.error("[APP] Couldn't update application user interface: "+e.getMessage());
            updated = false;
        }
        logger.debug("[APP] Deleted old updatar json: "+new File(getApplicationPath() + "updater.json").delete());
        logger.debug("[APP] Deleted older updater json: "+new File(getApplicationPath() + "version.json").delete());
        return updated;
    }

    // Method to launch the application
    public void launch() {
        moduleLoader.activateModules();
        frame.setVisible(true);
    }
}