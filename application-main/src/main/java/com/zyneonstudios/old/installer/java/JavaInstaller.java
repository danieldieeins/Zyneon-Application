package com.zyneonstudios.old.installer.java;

import com.zyneonstudios.Main;
import live.nerotv.shademebaby.file.OnlineConfig;
import live.nerotv.shademebaby.utils.FileUtil;

import java.io.File;

public class JavaInstaller {

    private Java runtimeVersion;
    private OperatingSystem operatingSystem;
    private Architecture architecture;

    public JavaInstaller() {
        runtimeVersion = null;
        operatingSystem = null;
        architecture = null;
    }

    public JavaInstaller(Java runtimeVersion, OperatingSystem operatingSystem, Architecture architecture) {
        this.runtimeVersion = runtimeVersion;
        this.operatingSystem = operatingSystem;
        this.architecture = architecture;
    }

    public String getVersionString() {
        String os = "null-";
        if(operatingSystem!=null) {
            os = operatingSystem.toString().toLowerCase()+"-";
        }
        String a = "null_";
        if(architecture!=null) {
            a = architecture.toString().toLowerCase()+"_";
        }
        String jre = "jre-null";
        if(runtimeVersion!=null) {
            if(runtimeVersion.equals(Java.Runtime_8)) {
                jre = "jre-8";
            } else if(runtimeVersion.equals(Java.Runtime_11)) {
                jre = "jre-11";
            } else {
                jre = "jre-21";
            }
        }
        return os+a+jre;
    }

    public Architecture getArchitecture() {
        return architecture;
    }

    public OperatingSystem getOperatingSystem() {
        return operatingSystem;
    }

    public Java getRuntimeVersion() {
        return runtimeVersion;
    }

    public void setArchitecture(Architecture architecture) {
        this.architecture = architecture;
    }

    public void setOperatingSystem(OperatingSystem operatingSystem) {
        this.operatingSystem = operatingSystem;
    }

    public void setRuntimeVersion(Java runtimeVersion) {
        this.runtimeVersion = runtimeVersion;
    }

    public void install() {
        String versionString = getVersionString();
        if(versionString.contains("null")) {
            throw new NullPointerException("Couldn't find such a java version");
        } else {
            Main.getLogger().debug("[INSTALLER] (JAVA) Gathering java information...");
            OnlineConfig index = new OnlineConfig("https://raw.githubusercontent.com/danieldieeins/ZyneonApplicationContent/main/l/application.json");
            String download = index.getString("runtime."+versionString);
            String zipPath = Main.getDirectoryPath()+"libs/"+runtimeVersion+".zip";
            Main.getLogger().debug("[INSTALLER] (JAVA) Starting download from "+download+" to "+zipPath+"...");
            FileUtil.downloadFile(download, zipPath);
            FileUtil.unzipFile(zipPath,Main.getDirectoryPath()+"libs/");
            Main.getLogger().debug("[INSTALLER] (JAVA) Deleted zip-File: "+new File(zipPath).delete());
            Main.getLogger().log("[INSTALLER] (JAVA) Installed Java Runtime: "+versionString+"!");
        }
    }
}