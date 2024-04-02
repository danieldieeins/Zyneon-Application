package com.zyneonstudios.application.launcher;

import com.zyneonstudios.Main;
import com.zyneonstudios.application.installer.java.Java;
import com.zyneonstudios.application.installer.java.JavaInstaller;
import com.zyneonstudios.application.utils.backend.MinecraftVersion;
import fr.theshark34.openlauncherlib.JavaUtil;

import java.io.File;

public class Launcher {

    public static void setJava(MinecraftVersion.Type type) {
        Main.getLogger().log("[LAUNCHER] Detected Minecraft version type "+type+"!");
        if(type.equals(MinecraftVersion.Type.LEGACY)) {
            JavaUtil.setJavaCommand(null);
            String java = Main.getDirectoryPath()+"libs/jre-8/";
            if(!new File(java).exists()) {
                Main.getLogger().error("[LAUNCHER] Couldn't find compatible Java Runtime Environment!");
                JavaInstaller javaInstaller = new JavaInstaller(Java.Runtime_8,Main.operatingSystem,Main.architecture);
                javaInstaller.install();
                Main.getLogger().debug("[LAUNCHER] Starting installation of missing java runtime "+javaInstaller.getVersionString()+"...");
            }
            System.setProperty("java.home", java);
        } else if(type.equals(MinecraftVersion.Type.SEMI_NEW)) {
            JavaUtil.setJavaCommand(null);
            String java = Main.getDirectoryPath()+"libs/jre-11/";
            if(!new File(java).exists()) {
                Main.getLogger().error("[LAUNCHER] Couldn't find compatible Java Runtime Environment!");
                JavaInstaller javaInstaller = new JavaInstaller(Java.Runtime_11,Main.operatingSystem,Main.architecture);
                javaInstaller.install();
                Main.getLogger().debug("[LAUNCHER] Starting installation of missing java runtime "+javaInstaller.getVersionString()+"...");
            }
            System.setProperty("java.home", java);
        } else if(type.equals(MinecraftVersion.Type.NEW)) {
            JavaUtil.setJavaCommand(null);
            String java = Main.getDirectoryPath()+"libs/jre/";
            if(!new File(java).exists()) {
                Main.getLogger().error("[LAUNCHER] Couldn't find compatible Java Runtime Environment!");
                JavaInstaller javaInstaller = new JavaInstaller(Java.Runtime_21,Main.operatingSystem,Main.architecture);
                javaInstaller.install();
                Main.getLogger().debug("[LAUNCHER] Starting installation of missing java runtime "+javaInstaller.getVersionString()+"...");
            }
            System.setProperty("java.home", java);
        }
    }
}
