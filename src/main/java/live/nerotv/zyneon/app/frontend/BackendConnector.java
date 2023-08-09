package live.nerotv.zyneon.app.frontend;

import live.nerotv.zyneon.app.backend.installer.ForgeInstaller;

public class BackendConnector {

    public void callJavaMethod(String message) {
        System.out.println(message);
        if(message.equals("button.account")) {
            //Für plocc: klasse.openAccountWindow(); (oder so)
        } else if(message.contains("button.start.")) {
            String modpackid = message.replace("button.start.","");
            //Für plocc: andereKlasse.startModpack(modpackid);
            new ForgeInstaller().installForge("1.20.1","47.1.43");
        }
    }
}
