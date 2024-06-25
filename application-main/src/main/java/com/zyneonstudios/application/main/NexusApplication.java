package com.zyneonstudios.application.main;

import com.formdev.flatlaf.FlatDarkLaf;
import com.zyneonstudios.Main;
import com.zyneonstudios.application.frame.web.ApplicationFrame;
import com.zyneonstudios.application.frame.web.CustomApplicationFrame;
import com.zyneonstudios.application.modules.ModuleLoader;
import com.zyneonstudios.application.modules.localcommands.LocalCommandsModule;
import live.nerotv.shademebaby.logger.Logger;
import live.nerotv.shademebaby.utils.FileUtil;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.net.URL;
import java.security.CodeSource;

import static com.zyneonstudios.application.main.ApplicationConfig.getApplicationPath;

public class NexusApplication {

    /*
     * Zyneon Application "main" object
     * by nerotvlive
     * Contributions are welcome. Please add your name to the "by" line if you make any modifications.
     * */

    private final JFrame frame;
    private static final Logger logger = new Logger("APP");
    private static ModuleLoader moduleLoader = null;

    public NexusApplication() {
        // Initializing the application frame
        moduleLoader = new ModuleLoader(this);
        logger.log("[APP] Updated application ui: "+update());
        try {
            FlatDarkLaf.setup();
            UIManager.setLookAndFeel(new FlatDarkLaf());
        } catch (Exception ignore) {}
        if(ApplicationConfig.getOS().startsWith("macOS")||ApplicationConfig.getOS().startsWith("Windows")) {
            // Creating a standard application frame for macOS and Windows
            frame = new ApplicationFrame(this, ApplicationConfig.urlBase + ApplicationConfig.language + "/" + ApplicationConfig.startPage, getApplicationPath() + "libs/jcef/");
            frame.pack(); frame.setSize(new Dimension(1200,720));
        } else {
            // Creating a custom application frame for other operating systems
            frame = new CustomApplicationFrame(this, ApplicationConfig.urlBase + ApplicationConfig.language + "/" + ApplicationConfig.startPage, getApplicationPath() + "libs/jcef/");
            frame.pack();
        }
        frame.setLocationRelativeTo(null);
        moduleLoader.loadModule(new LocalCommandsModule(this));

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

    // Method to extract the application UI
    private static boolean update() {
        File temp = new File(getApplicationPath() + "temp");
        if(temp.exists()) {
            if(temp.isDirectory()) {
                FileUtil.deleteFolder(temp);
            } else {
                logger.debug("[APP] Deleted temporary files: "+temp.delete());
            }
        }
        boolean updated;
        try {
            if(!new File(getApplicationPath() + "temp/modules/").exists()) {
                logger.debug("[APP] Created modules path: "+new File(getApplicationPath() + "temp/modules/").mkdirs());
            }
            FileUtil.extractResourceFile("modules.zip",getApplicationPath()+"temp/modules.zip",NexusApplication.class);
            FileUtil.unzipFile(getApplicationPath()+"temp/modules.zip", getApplicationPath() + "temp/modules/");
            logger.debug("[APP] Deleted modules archive: "+new File(getApplicationPath()+"temp/modules.zip").delete());
            File modules = new File(getApplicationPath() + "temp/modules/");
            if(modules.exists()) {
                if(modules.isDirectory()) {
                    for(File module : modules.listFiles()) {
                        if(module.getName().toLowerCase().endsWith(".jar")) {
                            try {
                                moduleLoader.loadModule(moduleLoader.readModule(module));
                            } catch (Exception e) {
                                getLogger().error("Couldn't load module "+module.getName()+": "+e.getMessage());
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.error("[APP] Couldn't extract modules: "+e.getMessage());
        }
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
        if(Main.splash!=null) {
            Main.splash.setVisible(false);
            Main.splash = null;
            System.gc();
        }
    }

    //Method to close the application and start the updater (restart method)
    public void restart() {
        CodeSource codeSource = Main.class.getProtectionDomain().getCodeSource();
        if (codeSource != null) {
            URL jarUrl = codeSource.getLocation();
            String jarPath = jarUrl.getPath();
            if(jarPath.startsWith("/")) {
                jarPath = jarPath.replaceFirst("/","");
            }
            StringBuilder args = new StringBuilder();
            if(ApplicationConfig.getArguments()!=null) {
                for(String arg : ApplicationConfig.getArguments()) {
                    args.append(arg).append(" ");
                }
            }
            File updater = new File(ApplicationConfig.getApplicationPath().replace("\\","/").replace("/experimental/","/app.jar"));
            if(updater.exists()) {
                jarPath = updater.getAbsolutePath();
            }
            ProcessBuilder pb = new ProcessBuilder("java", "-jar", jarPath, args.toString());
            try {
                pb.start();
            } catch (Exception e) {
                logger.error("[APP] Couldn't restart application: "+e.getMessage());
            }
            stop();
        }
        System.exit(-1);
    }

    //Default stop closing method
    public static void stop() {
        moduleLoader.deactivateModules();
        System.exit(0);
    }
}