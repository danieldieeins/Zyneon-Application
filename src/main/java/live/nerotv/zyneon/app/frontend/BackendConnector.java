package live.nerotv.zyneon.app.frontend;

import fr.flowarg.flowupdater.versions.ForgeVersionType;
import live.nerotv.Main;
import live.nerotv.zyneon.app.backend.launcher.FabricLauncher;
import live.nerotv.zyneon.app.backend.launcher.ForgeLauncher;
import live.nerotv.zyneon.app.backend.launcher.VanillaLauncher;
import live.nerotv.zyneon.app.backend.login.MicrosoftAuth;
import java.nio.file.Path;

public class BackendConnector {

    private final FabricLauncher fabricLauncher = new FabricLauncher();
    private final ForgeLauncher forgeLauncher = new ForgeLauncher();
    private final VanillaLauncher vanillaLauncher = new VanillaLauncher();

    public void callJavaMethod(String message) {
        if(message.equals("button.account")) {
            System.out.println(message);
            MicrosoftAuth.authenticateMS();
        } else if(message.contains("button.start.")) {
            String modpackid = message.replace("button.start.","");
            if(modpackid.equals("argria2")) {
                forgeLauncher.launch("1.18.2","40.2.10", ForgeVersionType.NEW,16384, Path.of(Main.getDirectoryPath()+"instances/official/argria2/"));
            } else if(modpackid.contains("zyneonplus")) {
                if(modpackid.contains("1194")) {
                    fabricLauncher.launch("1.19.4","0.14.22",8192,Path.of(Main.getDirectoryPath()+"instances/official/zyneonplus/1.19.4/"));
                } else if(modpackid.contains("1201")) {
                    fabricLauncher.launch("1.20.1","0.14.22",8192,Path.of(Main.getDirectoryPath()+"instances/official/zyneonplus/1.20.1/"));
                }
            }
            System.out.println(message);
        }
    }
}
