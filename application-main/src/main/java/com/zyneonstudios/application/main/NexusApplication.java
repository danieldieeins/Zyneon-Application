package com.zyneonstudios.application.main;

import com.formdev.flatlaf.FlatDarkLaf;
import com.google.gson.JsonObject;
import com.zyneonstudios.Main;
import com.zyneonstudios.application.download.Download;
import com.zyneonstudios.application.download.DownloadManager;
import com.zyneonstudios.application.frame.web.ApplicationFrame;
import com.zyneonstudios.application.frame.web.CustomApplicationFrame;
import com.zyneonstudios.application.modules.ModuleLoader;
import live.nerotv.shademebaby.logger.Logger;
import live.nerotv.shademebaby.utils.FileUtil;
import live.nerotv.shademebaby.utils.GsonUtil;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.security.CodeSource;
import java.util.ArrayList;
import java.util.Objects;

public class NexusApplication {

    private final JFrame frame;
    private static final Logger logger = new Logger("APP");
    private static ModuleLoader moduleLoader = null;

    private final ApplicationRunner runner;
    private final DownloadManager downloadManager;

    public NexusApplication(String[] args) {
        try {
            FlatDarkLaf.setup();
            UIManager.setLookAndFeel(new FlatDarkLaf());
        } catch (Exception ignore) {}
        new ApplicationStorage(args,this);
        moduleLoader = new ModuleLoader(this);
        logger.log("[APP] Updated application ui: "+update());
        boolean disableCustomFrame = false;
        if(ApplicationStorage.getSettings().get("settings.linux.customFrame")!=null) {
            try {
                disableCustomFrame = !ApplicationStorage.getSettings().getBool("settings.linux.customFrame");
            } catch (Exception ignore) {}
        }
        String startPage = ApplicationStorage.startPage;
        if(ApplicationStorage.getSettings().get("settings.setupFinished")==null) {
            ApplicationStorage.getSettings().set("settings.setupFinished",false);
        }
        try {
            if(!ApplicationStorage.getSettings().getBool("settings.setupFinished")) {
                startPage = "firstrun.html";
            }
        } catch (Exception ignore) {}
        if(ApplicationStorage.getSettings().get("cache.restartPage")!=null) {
            try {
                startPage = ApplicationStorage.getSettings().get("cache.restartPage").toString();
                ApplicationStorage.getSettings().delete("cache.restartPage");
            } catch (Exception ignore) {}
        }
        if(ApplicationStorage.getOS().startsWith("macOS")|| ApplicationStorage.getOS().startsWith("Windows")||disableCustomFrame) {
            frame = new ApplicationFrame(this, ApplicationStorage.urlBase + ApplicationStorage.language + "/" + startPage, ApplicationStorage.getApplicationPath() + "libs/jcef/");
            frame.pack(); frame.setSize(new Dimension(1200,720));
        } else {
            JFrame frame_ = null;
            try {
                frame_ = new CustomApplicationFrame(this, ApplicationStorage.urlBase + ApplicationStorage.language + "/" + startPage, ApplicationStorage.getApplicationPath() + "libs/jcef/");
                frame_.pack(); frame_.setSize(new Dimension(1150,700));
            } catch (Exception e) {
                logger.error("[APP] Couldn't load custom Linux frame: "+e.getMessage());
                logger.error("[APP] Disabling custom Linux frame and restarting...");
                ApplicationStorage.getSettings().set("settings.linux.customFrame",false);
                restart(false);
            }
            frame = frame_;
        }
        if(frame==null) {
            System.exit(-1);;
        }
        frame.setLocationRelativeTo(null);

        this.runner = new ApplicationRunner(this);
        this.runner.start();
        this.downloadManager = new DownloadManager(this);

        logger.log("[APP] Updated application modules: "+updateModules());
        File modules = new File(ApplicationStorage.getApplicationPath()+"modules/");
        if(modules.exists()) {
            if(modules.isDirectory()) {
                try {
                    for(File module : Objects.requireNonNull(modules.listFiles())) {
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

    public DownloadManager getDownloadManager() {
        return downloadManager;
    }

    public ApplicationRunner getRunner() {
        return runner;
    }

    public static Logger getLogger() {
        return logger;
    }

    public static ModuleLoader getModuleLoader() {
        return moduleLoader;
    }

    public JFrame getFrame() {
        return frame;
    }

    private boolean update() {

        // TRYING TO DELETE OLD TEMP FOLDER
        File temp = new File(ApplicationStorage.getApplicationPath() + "temp");
        if(temp.exists()) {
            if(temp.isDirectory()) {
                FileUtil.deleteFolder(temp);
            } else {
                logger.debug("[APP] Deleted temporary files: "+temp.delete());
            }
        }

        // UI UPDATE
        boolean updated;
        try {
            if(new File(ApplicationStorage.getApplicationPath() + "temp/ui/").exists()) {
                try {
                    FileUtil.deleteFolder(new File(ApplicationStorage.getApplicationPath() + "temp/ui/"));
                } catch (Exception e) {
                    getLogger().error("Couldn't delete old temporary ui files: "+e.getMessage());
                }
            }
            logger.debug("[APP] Created new ui path: "+new File(ApplicationStorage.getApplicationPath() + "temp/ui/").mkdirs());
            FileUtil.extractResourceFile("content.zip", ApplicationStorage.getApplicationPath()+"temp/content.zip",Main.class);
            FileUtil.unzipFile(ApplicationStorage.getApplicationPath()+"temp/content.zip", ApplicationStorage.getApplicationPath() + "temp/ui");
            logger.debug("[APP] Deleted ui archive: "+new File(ApplicationStorage.getApplicationPath()+"temp/content.zip").delete());
            updated = true;
        } catch (Exception e) {
            logger.error("[APP] Couldn't update application user interface: "+e.getMessage());
            updated = false;
        }
        logger.debug("[APP] Deleted old updatar json: "+new File(ApplicationStorage.getApplicationPath() + "updater.json").delete());
        logger.debug("[APP] Deleted older updater json: "+new File(ApplicationStorage.getApplicationPath() + "version.json").delete());
        return updated;
    }

    @SuppressWarnings("unchecked")
    private boolean updateModules() {
        if(ApplicationStorage.test) {
            return true;
        }
        boolean updated = false;
        File modules = new File(ApplicationStorage.getApplicationPath() + "temp/modules/");
        if (modules.exists()) {
            FileUtil.deleteFolder(modules);
        }
        logger.debug("[APP] Created modules path: " + modules.mkdirs());

        if(!ApplicationStorage.isOffline()) {
            try {
                ArrayList<String> disabledIds = new ArrayList<>();
                if(ApplicationStorage.getSettings().get("settings.modules.disabledIds")!=null) {
                    disabledIds = (ArrayList<String>) ApplicationStorage.getSettings().get("settings.modules.disabledIds");
                }

                downloadModule("https://zyneonstudios.github.io/nexus-nex/zyndex/modules/official/nexus-minecraft-module.json",modules,disabledIds);
                downloadModule("https://zyneonstudios.github.io/nexus-nex/zyndex/modules/official/zyneon-star-module.json",modules,disabledIds);

                updated = true;
            } catch (Exception e) {
                logger.error("[APP] Couldn't update online modules: "+e.getMessage());
            }
        }

        if(!updated) {
            try {
                FileUtil.extractResourceFile("modules.zip", ApplicationStorage.getApplicationPath() + "temp/modules.zip", NexusApplication.class);
                FileUtil.unzipFile(ApplicationStorage.getApplicationPath() + "temp/modules.zip", ApplicationStorage.getApplicationPath() + "temp/modules/");
                logger.debug("[APP] Deleted modules archive: " + new File(ApplicationStorage.getApplicationPath() + "temp/modules.zip").delete());
            } catch (Exception e) {
                logger.error("[APP] Couldn't extract fallback modules: " + e.getMessage());
            }
        }

        if (modules.exists()) {
            if (modules.isDirectory()) {
                for (File module : Objects.requireNonNull(modules.listFiles())) {
                    if (module.getName().toLowerCase().endsWith(".jar")) {
                        try {
                            moduleLoader.loadModule(moduleLoader.readModule(module));
                        } catch (Exception e) {
                            getLogger().error("Couldn't load module " + module.getName() + ": " + e.getMessage());
                        }
                    }
                }
            }
        }

        return updated;
    }

    private void downloadModule(String jsonUrl, File folder, ArrayList<String> blacklist) throws MalformedURLException {
        JsonObject module = GsonUtil.getObject(jsonUrl).getAsJsonObject("module");
        String id = module.getAsJsonObject("meta").get("id").getAsString();
        if(!blacklist.contains(id)) {
            Download download = new Download(id+".jar",new URL(module.getAsJsonObject("meta").get("download").getAsString()), Path.of(folder.getAbsolutePath()+"/"+id+".jar"));
            download.start();
        }
    }

    public void launch() {
        moduleLoader.activateModules();
        frame.setVisible(true);
        if(Main.splash!=null) {
            Main.splash.setVisible(false);
            Main.splash = null;
            System.gc();
        }
    }

    public void restart(boolean soft) {
        CodeSource codeSource = Main.class.getProtectionDomain().getCodeSource();
        if(soft) {
            if (codeSource != null) {
                URL jarUrl = codeSource.getLocation();
                String jarPath = jarUrl.getPath();
                if(!ApplicationStorage.getOS().startsWith("Linux")) {
                    if (jarPath.startsWith("/")) {
                        jarPath = jarPath.replaceFirst("/", "");
                    }
                }
                StringBuilder args = new StringBuilder();
                if(ApplicationStorage.getArguments()!=null) {
                    for(String arg : ApplicationStorage.getArguments()) {
                        args.append(arg).append(" ");
                    }
                }
                ProcessBuilder pb = new ProcessBuilder("java", "-jar", jarPath, args.toString());
                try {
                    pb.start();
                } catch (Exception e) {
                    logger.error("[APP] Couldn't restart application: "+e.getMessage());
                }
                getModuleLoader().deactivateModules();
                System.exit(0);
            }
        } else {
            if (codeSource != null) {
                URL jarUrl = codeSource.getLocation();
                String jarPath = jarUrl.getPath();
                if(!ApplicationStorage.getOS().startsWith("Linux")) {
                    if (jarPath.startsWith("/")) {
                        jarPath = jarPath.replaceFirst("/", "");
                    }
                }
                StringBuilder args = new StringBuilder();
                if (ApplicationStorage.getArguments() != null) {
                    for (String arg : ApplicationStorage.getArguments()) {
                        args.append(arg).append(" ");
                    }
                }
                File updater = new File(ApplicationStorage.getApplicationPath().replace("\\", "/").replace("/experimental/", "/app.jar"));
                if (updater.exists()) {
                    jarPath = updater.getAbsolutePath();
                }
                ProcessBuilder pb = new ProcessBuilder("java", "-jar", jarPath, args.toString());
                try {
                    pb.start();
                } catch (Exception e) {
                    logger.error("[APP] Couldn't restart application: " + e.getMessage());
                }
                stop();
            }
        }
        System.exit(-1);
    }

    public static void stop() {
        moduleLoader.deactivateModules();
        FileUtil.deleteFolder(new File(ApplicationStorage.getApplicationPath() + "temp/"));
        System.exit(0);
    }
}