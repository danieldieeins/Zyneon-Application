package live.nerotv.zyneon.app.frontend;

import live.nerotv.zyneon.app.backend.installer.ForgeInstaller;
import live.nerotv.zyneon.app.backend.login.MicrosoftAuth;

public class BackendConnector {

    public void callJavaMethod(String message) {
        if(message.equals("button.account")) {
            System.out.println(message);
            MicrosoftAuth.authenticateMS();
        } else if(message.contains("button.start.")) {
            String modpackid = message.replace("button.start.","");
            //Für plocc: andereKlasse.startModpack(modpackid);
            new ForgeInstaller().installForge("1.20.1","47.1.43");
            System.out.println(message);
        }
    }
}
