package live.nerotv.zyneon.launcher.frontend;

public class BackendConnector {

    public void callJavaMethod(String message) {
        if(message.equals("button.account")) {
            //Für plocc: klasse.openAccountWindow(); (oder so)
        } else if(message.contains("button.start.")) {
            String modpackid = message.replace("button.start.","");
            //Für plocc: andereKlasse.startModpack(modpackid);
        }
    }
}
