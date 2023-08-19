package live.nerotv.zyneon.app.frontend;

import live.nerotv.Main;
import live.nerotv.openlauncherapi.auth.SimpleMicrosoftAuth;
import live.nerotv.zyneon.app.backend.login.MicrosoftAuth;
import live.nerotv.zyneon.app.backend.modpack.FabricPack;
import live.nerotv.zyneon.app.backend.modpack.ForgePack;

import java.awt.*;
import java.net.URI;

public class BackendConnectorV2 {

    public void resolveRequest(String request) {
        if(request.equals("button.accessmode")) {
            if(Main.auth.isLoggedIn()) {
                if(Main.us.contains(Main.auth.getAuthInfos().getUuid())) {
                    Main.frame.getBrowser().loadURL("https://a.nerotv.live/zyneon/application/html/access.html");
                }
            }
        } else if(request.equals("button.normalmode")) {
            Main.frame.getBrowser().loadURL(Main.getURL());
        } else if(request.equals("button.account")) {
            Main.auth.startAsyncWebview();
        } else if(request.contains("button.logout")) {
            System.out.println("0");
            if(Main.auth.isLoggedIn()) {
                System.out.println("1");
                Main.auth.getSaveFile().delete();
                Main.auth = new SimpleMicrosoftAuth();
                MicrosoftAuth.login();
                if(Main.auth.isLoggedIn()) {
                    Main.frame.setTitle("Zyneon Application ("+Main.getVersion()+", "+Main.auth.getAuthInfos().getUsername()+")");
                } else {
                    Main.frame.setTitle("Zyneon Application ("+Main.getVersion()+")");
                }
            } else {
                Main.frame.getBrowser().executeJavaScript("javascript:OpenModal('notLoggedIn')","https://a.nerotv.live/zyneon/application/html/account.html",5);
            }
        } else if(request.contains("button.labynet")) {
            if(Main.auth.isLoggedIn()) {
                String url = "https://laby.net/@"+Main.auth.getAuthInfos().getUsername();
                if (Desktop.isDesktopSupported()) {
                    Desktop desktop = Desktop.getDesktop();
                    if (desktop.isSupported(Desktop.Action.BROWSE)) {
                        try {
                            desktop.browse(new URI(url));
                        } catch (Exception ignore) {}
                    }
                }
            } else {
                Main.frame.getBrowser().executeJavaScript("javascript:OpenModal('notLoggedIn')","https://a.nerotv.live/zyneon/application/html/account.html",5);
            }
        } else if(request.contains("button.start.")) {
            if(Main.auth.isLoggedIn()) {
                String id = request.replace("button.start.","");
                startInstance(id);
            } else {
                Main.frame.getBrowser().executeJavaScript("javascript:OpenModal('notLoggedIn')","https://a.nerotv.live/zyneon/application/html/account.html",5);
            }
        }
    }

    public void startInstance(String id) {
        if(id.contains("argria2")) {
            Main.getForgeLauncher().launch(new ForgePack("https://a.nerotv.live/zyneon/application/modpack/argria2.json"),8192*2);
        } else if(id.contains("zyneonplus")) {
            startZyneonPlus(id.replace("button","").replace("zyneonplus","").replace("start","").replace(".",""));
        } else if(id.contains("ukzplite")) {
            Main.getForgeLauncher().launch(new ForgePack("https://a.nerotv.live/zyneon/application/modpack/ukzplite.json"),8192/2);
        } else if(id.contains("ukzp")) {
            Main.getForgeLauncher().launch(new ForgePack("https://a.nerotv.live/zyneon/application/modpack/ukzp.json"),8192*2);
        } else if(id.contains("primal3de")) {
            Main.getForgeLauncher().launch(new ForgePack("https://a.nerotv.live/zyneon/application/modpack/primal3de.json"),8192*2);
        } else if(id.contains("primal")) {
            Main.getForgeLauncher().launch(new ForgePack("https://a.nerotv.live/zyneon/application/modpack/primal.json"),8192*2);
        } else if(id.contains("projectz2")) {
            Main.getForgeLauncher().launch(new ForgePack("https://a.nerotv.live/zyneon/application/modpack/projectz2.json"),8192*2);
        } else if(id.contains("projectz3")) {
            Main.getForgeLauncher().launch(new ForgePack("https://a.nerotv.live/zyneon/application/modpack/projectz3.json"),8192*2);
        } else if(id.contains("zyneontools")) {
            Main.getForgeLauncher().launch(new ForgePack("https://a.nerotv.live/zyneon/application/modpack/zyneontools.json"),8192*2);
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