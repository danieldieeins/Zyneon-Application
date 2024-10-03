package com.zyneonstudios.nexus.application.bootstrapper;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ApplicationBootstrapper {

    private static File appFolder = null;
    private static String updateChannel = null;

    public static void main(String[] args) {
        resolveArguments(args);
        if(appFolder == null) {
            appFolder = getDefaultFolder(false);
        }
    }

    private static void resolveArguments(String[] args) {
        int size = args.length;
        int self = 0;
        for(String arg:args) {
            int next = self+1;
            if(arg.startsWith("--")) {
                arg = arg.replaceFirst("--","");
                if(arg.equals("path")) {
                    if(next<size) {
                        try {
                            String path = args[next];
                            File folder = new File(path);
                            if(!folder.exists()) {
                                if(!folder.mkdirs()) {
                                    throw new RuntimeException("Couldn't create app path");
                                }
                            }
                            appFolder = folder;
                        } catch (Exception e) {
                            System.err.println("Invalid path specified. ("+e.getMessage()+")");
                            System.exit(1);
                        }
                    } else {
                        System.err.println("No path specified.");
                        System.exit(1);
                    }
                } else if(arg.equals("channel")) {
                    if(next<size) {
                        updateChannel = args[next];
                    } else {
                        System.err.println("No update channel specified.");
                        System.exit(1);
                    }
                }
            }
            self=next;
        }
    }

    private static File getDefaultFolder(boolean old) {
        String folderName = "Zyneon/NEXUS App";
        if(old) {
            folderName = "Zyneon/Application";
        }
        String appData;
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) {
            appData = System.getenv("LOCALAPPDATA");
        } else if (os.contains("mac")) {
            appData = System.getProperty("user.home") + "/Library/Application Support";
        } else {
            appData = System.getProperty("user.home") + "/.local/share";
        }
        Path folderPath = Paths.get(appData, folderName);
        try {
            Files.createDirectories(folderPath);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
        String applicationPath = folderPath.toString().replace("\\","/") + "/";

        File folder = new File(applicationPath);
        if(!folder.exists()) {
            if (!folder.mkdirs()) {
                System.err.println("Couldn't create application path.");
                System.exit(1);
            }
        }
        return folder;
    }

    public static File getAppFolder() {
        return appFolder;
    }

    public static String getUpdateChannel() {
        return updateChannel;
    }
}
