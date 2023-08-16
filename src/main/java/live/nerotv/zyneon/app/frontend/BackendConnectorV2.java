package live.nerotv.zyneon.app.frontend;

import live.nerotv.Main;
import live.nerotv.zyneon.app.backend.modpack.FabricPack;
import live.nerotv.zyneon.app.backend.modpack.ForgePack;

public class BackendConnectorV2 {

    public void resolveRequest(String request) {
        if(request.equals("button.account")) {
            Main.getAuth().startAsyncWebview();
        } else if(request.contains("button.start.")) {
            String id = request.replace("button.start.","");
            startInstance(id);
        }
    }

    public void startInstance(String id) {
        if(id.contains("argria2")) {
            Main.getForgeLauncher().launch(new ForgePack("https://a.nerotv.live/zyneon/application/modpack/argria2.json"),8192*2);
        } else if(id.contains("zyneonplus")) {
            startZyneonPlus(id.replace("button","").replace("zyneonplus","").replace("start","").replace(".",""));
        }
    }

    public void startZyneonPlus(String versionID) {
        if(versionID.equalsIgnoreCase("1165")) {
            Main.getFabricLauncher().launch(new FabricPack("https://a.nerotv.live/zyneon/application/modpack/zyneonplus/1.16.5.json"),8192);
        } else if(versionID.equalsIgnoreCase("1171")) {
            Main.getFabricLauncher().launch(new FabricPack("https://a.nerotv.live/zyneon/application/modpack/zyneonplus/1.17.1.json"),8192);
        } else if(versionID.equalsIgnoreCase("1182")) {
            Main.getFabricLauncher().launch(new FabricPack("https://a.nerotv.live/zyneon/application/modpack/zyneonplus/1.18.2.json"),8192);
        } else if(versionID.equalsIgnoreCase("1194")) {
            Main.getFabricLauncher().launch(new FabricPack("https://a.nerotv.live/zyneon/application/modpack/zyneonplus/1.19.4.json"),8192);
        } else if(versionID.equalsIgnoreCase("1201")) {
            Main.getFabricLauncher().launch(new FabricPack("https://a.nerotv.live/zyneon/application/modpack/zyneonplus/1.20.1.json"),8192);
        }
    }
}