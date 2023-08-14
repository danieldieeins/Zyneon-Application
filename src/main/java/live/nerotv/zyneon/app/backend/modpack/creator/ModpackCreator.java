package live.nerotv.zyneon.app.backend.modpack.creator;

import live.nerotv.Main;
import live.nerotv.zyneon.app.backend.utils.Config;

import java.io.File;
import java.util.Scanner;

public class ModpackCreator {

    private String id;
    private String name;
    private String version; //modpack version
    private String minecraftVersion;
    private String fabricVersion;
    private String forgeVersion;
    private String forgeType;
    private String downloadURL;
    private String instancePath;

    private Scanner scanner;

    public ModpackCreator() {
        scanner = new Scanner(System.in);
    }

    public void start() {
        askID();
    }

    public void askID() {
        System.out.println("Wie soll die Modpack-ID lauten?");
        id = scanner.nextLine().toLowerCase();
        if(id.equalsIgnoreCase("")) {
            System.out.println("Diese ID ist ungültig.");
            askID();
        } else {
            askName();
        }
    }

    public void askName() {
        System.out.println("Wie soll der Modpack-Name lauten?");
        name = scanner.nextLine();
        if(name.equalsIgnoreCase("")) {
            System.out.println("Dieser Name ist ungültig.");
            askName();
        } else {
            askVersion();
        }
    }

    public void askVersion() {
        System.out.println("Wie soll die Modpack-Version lauten?");
        version = scanner.nextLine();
        if(version.equalsIgnoreCase("")) {
            System.out.println("Diese Version ist ungültig.");
            askVersion();
        } else {
            askMinecraftVersion();
        }
    }

    public void askMinecraftVersion() {
        System.out.println("Welche Minecraft-Version soll genutzt werden?");
        minecraftVersion = scanner.nextLine();
        if(minecraftVersion.equalsIgnoreCase("")) {
            System.out.println("Diese Version ist ungültig.");
            askMinecraftVersion();
        } else {
            askFabricVersion();
        }
    }

    public void askFabricVersion() {
        System.out.println("Welche Fabric-Version soll genutzt werden? (Leer lassen, wenn du Forge oder Vanilla nutzen willst)");
        fabricVersion = scanner.nextLine();
        if(fabricVersion.equalsIgnoreCase("")) {
            askForgeVersion();
        } else {
            finish();
        }
    }

    public void askForgeVersion() {
        System.out.println("Welche Forge-Version soll genutzt werden? (Leer lassen, wenn Vanilla nutzen willst)");
        forgeVersion = scanner.nextLine();
        if(forgeVersion.equalsIgnoreCase("")) {
            finish();
        } else {
            askForgeType();
        }
    }

    public void askForgeType() {
        if(forgeVersion.equalsIgnoreCase("")) {
            finish();
            return;
        }
        System.out.println("Welcher Forge-Typ soll genutzt werden? (NEW, NEO, OLD)");
        forgeType = scanner.nextLine();
        if(forgeType.equalsIgnoreCase("")) {
            System.out.println("Dieser Typ ist ungültig.");
            askForgeType();
        } else {
            finish();
        }
    }

    public void finish() {
        downloadURL = "https://a.nerotv.live/zyneon/application/modpack/"+id+"/"+version+".zip";
        instancePath = "instances/official/"+id+"/";
        System.out.println("Modpack: "+name+" ("+id+")");
        System.out.println("Version: "+version+" ("+minecraftVersion+")");
        System.out.println("Fabric: "+fabricVersion);
        System.out.println("Forge: "+forgeVersion+" ("+forgeType+")");
        System.out.println("Download: "+downloadURL);
        System.out.println("Instanz: "+instancePath);
        Config output = new Config(new File(Main.getDirectoryPath()+"modpackCreator/"+id+".json"));
        output.set("modpack.id",id);
        output.set("modpack.name",name);
        output.set("modpack.version",version);
        output.set("modpack.minecraft",minecraftVersion);
        if(fabricVersion.equalsIgnoreCase("")) {
            if(!forgeType.equalsIgnoreCase("")) {
                output.set("modpack.forge.version", forgeVersion);
                output.set("modpack.forge.type", forgeType);
            }
        } else {
            output.set("modpack.fabric",fabricVersion);
        }
        output.set("modpack.download",downloadURL);
        output.set("modpack.instance",instancePath);
        System.out.println("JSON exportiert nach "+output.getPath());
    }
}