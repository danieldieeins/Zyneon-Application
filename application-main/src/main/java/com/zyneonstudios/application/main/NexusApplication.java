package com.zyneonstudios.application.main;

import com.zyneonstudios.Main;
import com.zyneonstudios.application.download.DownloadManager;
import com.zyneonstudios.application.frame.ApplicationFrame;
import com.zyneonstudios.application.modules.ModuleLoader;
import com.zyneonstudios.nexus.desktop.frame.web.NexusWebSetup;
import com.zyneonstudios.nexus.utilities.NexusUtilities;
import com.zyneonstudios.nexus.utilities.file.FileActions;
import com.zyneonstudios.nexus.utilities.file.FileExtractor;
import com.zyneonstudios.nexus.utilities.logger.NexusLogger;
import me.friwi.jcefmaven.MavenCefAppHandlerAdapter;
import org.cef.CefApp;

import java.io.File;
import java.net.URL;
import java.security.CodeSource;
import java.util.Objects;

public class NexusApplication {

    private ApplicationFrame frame;

    private static final NexusLogger logger = NexusUtilities.getLogger();
    private static ModuleLoader moduleLoader = null;

    private final ApplicationRunner runner;
    private final DownloadManager downloadManager;

    public NexusApplication(String[] args) {
        new ApplicationStorage(args,this);
        frame = new ApplicationFrame();
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
        NexusWebSetup setup = new NexusWebSetup(ApplicationStorage.getApplicationPath()+"libraries/cef");
        setup.getBuilder().setAppHandler(new MavenCefAppHandlerAdapter() {
            @Override @Deprecated
            public void stateHasChanged(CefApp.CefAppState state) {
                if (state == CefApp.CefAppState.TERMINATED) {
                    NexusApplication.stop();
                }
                if(!ApplicationStorage.getOS().startsWith("Windows")) {
                    if(state == CefApp.CefAppState.SHUTTING_DOWN) {
                        NexusApplication.stop();
                    }
                }
            }
        });
        setup.enableCache(true); setup.enableCookies(true); setup.setup();

        this.runner = new ApplicationRunner(this);
        this.runner.start();
        this.downloadManager = new DownloadManager(this);

        File modules = new File(ApplicationStorage.getApplicationPath()+"modules/");
        if(modules.exists()) {
            if(modules.isDirectory()) {
                try {
                    for(File module : Objects.requireNonNull(modules.listFiles())) {
                        if(!module.isDirectory()) {
                            try {
                                moduleLoader.loadModule(moduleLoader.readModule(module));
                            } catch (Exception e) {
                                logger.dbg("[APP] Cant read module "+module.getName()+": "+e.getMessage());
                            }
                        }
                    }
                } catch (Exception e) {
                    logger.err("[APP] Can't read modules: "+e.getMessage());
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

    public static NexusLogger getLogger() {
        return logger;
    }

    public static ModuleLoader getModuleLoader() {
        return moduleLoader;
    }

    private boolean update() {

        // TRYING TO DELETE OLD TEMP FOLDER
        File temp = new File(ApplicationStorage.getApplicationPath() + "temp");
        if(temp.exists()) {
            if(temp.isDirectory()) {
                logger.dbg("[APP] Deleted temporary files: "+FileActions.deleteFolder(temp));
            } else {
                logger.dbg("[APP] Deleted temporary files: "+temp.delete());
            }
        }

        // UI UPDATE
        boolean updated;
        try {
            if(new File(ApplicationStorage.getApplicationPath() + "temp/ui/").exists()) {
                try {
                    FileActions.deleteFolder(new File(ApplicationStorage.getApplicationPath() + "temp/ui/"));
                } catch (Exception e) {
                    logger.err("Couldn't delete old temporary ui files: "+e.getMessage());
                }
            }
            logger.dbg("[APP] Created new ui path: "+new File(ApplicationStorage.getApplicationPath() + "temp/ui/").mkdirs());
            FileExtractor.extractResourceFile("content.zip", ApplicationStorage.getApplicationPath()+"temp/content.zip",Main.class);
            FileExtractor.unzipFile(ApplicationStorage.getApplicationPath()+"temp/content.zip", ApplicationStorage.getApplicationPath() + "temp/ui");
            logger.dbg("[APP] Deleted ui archive: "+new File(ApplicationStorage.getApplicationPath()+"temp/content.zip").delete());
            updated = true;
        } catch (Exception e) {
            logger.err("[APP] Couldn't update application user interface: "+e.getMessage());
            updated = false;
        }
        logger.dbg("[APP] Deleted old updatar json: "+new File(ApplicationStorage.getApplicationPath() + "updater.json").delete());
        logger.dbg("[APP] Deleted older updater json: "+new File(ApplicationStorage.getApplicationPath() + "version.json").delete());
        return updated;
    }

    public void launch() {
        frame.setVisible(true);
        //moduleLoader.activateModules();
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
                    logger.err("[APP] Couldn't restart application: "+e.getMessage());
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
                    logger.err("[APP] Couldn't restart application: " + e.getMessage());
                }
                stop();
            }
        }
        System.exit(-1);
    }

    public static void stop() {
        moduleLoader.deactivateModules();
        FileActions.deleteFolder(new File(ApplicationStorage.getApplicationPath() + "temp/"));
        System.exit(0);
    }
}