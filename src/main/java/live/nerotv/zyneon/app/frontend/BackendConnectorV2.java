package live.nerotv.zyneon.app.frontend;

import fr.flowarg.flowupdater.versions.ForgeVersionType;
import live.nerotv.Main;
import live.nerotv.zyneon.app.backend.login.MicrosoftAuth;

import java.nio.file.Path;

public class BackendConnectorV2 {

    public void resolveRequest(String request) {
        if(request.equals("button.account")) {
            MicrosoftAuth.authenticateMS();
        } else if(request.contains("button.start.")) {
            String id = request.replace("button.start.","");
            startInstance(id);
        }
    }

    public void startInstance(String id) {
        if(id.contains("argria2")) {
            Main.getForgeLauncher().launch("1.18.2","40.2.10", ForgeVersionType.NEW,16384, Path.of(Main.getDirectoryPath()+"instances/official/argria2/"));
        } else if(id.contains("zyneonplus")) {
            startZyneonPlus(id.replace("button","").replace("zyneonplus","").replace("start","").replace(".",""));
        }
    }

    public void startZyneonPlus(String versionID) {
        if(versionID.equalsIgnoreCase("189")) {
            Main.getForgeLauncher().launch("1.8.9","11.15.1.2318",ForgeVersionType.OLD,8192,Path.of(Main.getDirectoryPath()+"instances/official/zyneonplus/legacy/1.8.9/"));
        } else if(versionID.equalsIgnoreCase("1122")) {
            Main.getForgeLauncher().launch("1.12.2","14.23.5.2860",ForgeVersionType.OLD,8192,Path.of(Main.getDirectoryPath()+"instances/official/zyneonplus/legacy/1.12.2/"));
        } else if(versionID.equalsIgnoreCase("1165")) {
            Main.getFabricLauncher().launch("1.16.5","0.14.22",8192,Path.of(Main.getDirectoryPath()+"instances/official/zyneonplus/1.16.5/"));
        } else if(versionID.equalsIgnoreCase("1171")) {
            Main.getFabricLauncher().launch("1.17.1","0.14.22",8192,Path.of(Main.getDirectoryPath()+"instances/official/zyneonplus/1.17.1/"));
        } else if(versionID.equalsIgnoreCase("1182")) {
            Main.getFabricLauncher().launch("1.18.2","0.14.22",8192,Path.of(Main.getDirectoryPath()+"instances/official/zyneonplus/1.18.2/"));
        } else if(versionID.equalsIgnoreCase("1194")) {
            Main.getFabricLauncher().launch("1.19.4","0.14.22",8192,Path.of(Main.getDirectoryPath()+"instances/official/zyneonplus/1.19.4/"));
        } else if(versionID.equalsIgnoreCase("1201")) {
            Main.getFabricLauncher().launch("1.20.1","0.14.22",8192,Path.of(Main.getDirectoryPath()+"instances/official/zyneonplus/1.20.1/"));
        }
    }
}