package live.nerotv.zyneon.app.backend.installer;

import live.nerotv.Main;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

public class ForgeInstaller {

    public void installForge(String minecraftVersion, String forgeVersion) {
        System.out.println("attempting to install forge "+forgeVersion+" for minecraft "+minecraftVersion+"...");
        String installerPath = Main.getDirectoryPath()+"installer/forge/"+minecraftVersion+"/"+forgeVersion+".jar";
        String installFolder = Main.getDirectoryPath()+"installer/forge/"+minecraftVersion+"/"+forgeVersion+"-installed/";
        File installer = new File(installerPath);
        if(!installer.exists()) {
            new File(installer.getParent()).mkdirs();
            if(!downloadForge(minecraftVersion,forgeVersion)) {
                System.out.println("error: couldn't download forge - stopping installation...");
                return;
            }
        }
        if(!new File(installFolder).exists()) {
            new File(installFolder).mkdirs();
        }
        try {
            Process process = Runtime.getRuntime().exec("java -jar "+installerPath+" --installClient --installPath "+installFolder+" --mcversion "+minecraftVersion+" --forgeVersion "+forgeVersion);
            int exitCode = process.waitFor();
            System.out.println("Prozess beendet mit Exit-Code: " + exitCode);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean downloadForge(String minecraftVersion, String forgeVersion) {
        System.out.println("attempting to download forge "+forgeVersion+" for minecraft "+minecraftVersion+"...");
        String installerPath = Main.getDirectoryPath()+"installer/forge/"+minecraftVersion+"/"+forgeVersion+".jar";
        File installer = new File(installerPath);
        if(installer.exists()) {
            System.out.println("deleted existing installer: "+installer.delete());
        }
        System.out.println("folders created: "+new File(installer.getParent()).mkdirs());
        String installerURL = "https://files.minecraftforge.net/maven/net/minecraftforge/forge/"+minecraftVersion+"-"+forgeVersion+"/forge-"+minecraftVersion+"-"+forgeVersion+"-installer.jar";
        try {
            System.out.println("trying to download forge "+forgeVersion+" for "+minecraftVersion+" from: "+installerURL);
            installerPath = URLDecoder.decode(installerPath,StandardCharsets.UTF_8);
            BufferedInputStream inputStream = new BufferedInputStream(new URL(installerURL).openStream());
            FileOutputStream fileOutputStream = new FileOutputStream(installerPath);
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer, 0, 1024)) != -1) {
                fileOutputStream.write(buffer, 0, bytesRead);
            }
            fileOutputStream.close();
            inputStream.close();
            return true;
        } catch (IOException e) {
            System.out.println("error: failed to download forge "+forgeVersion+" for "+minecraftVersion+" from: "+installerURL);
            return false;
        }
    }
}