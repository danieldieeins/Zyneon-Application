package live.nerotv.zyneon.app.application.frontend.language;

import live.nerotv.Main;
import live.nerotv.zyneon.app.application.Application;

public class Language {

    public static void syncLanguage() {
        if(Main.language.equalsIgnoreCase("german")) {
            German.syncLanguage();
            return;
        }
        English.syncLanguage();
    }

    public static void sync(String path, String string) {
        path = path.replace("%","");
        path = "%"+path+"%";
        Application.getFrame().getBrowser().executeJavaScript("javascript:syncLanguage('" + path + "','" + string + "')", "https://danieldieeins.github.io/ZyneonApplicationContent/h/account.html", 5);
    }

    public static String getNotLoggedIn() {
        if(Main.language.equalsIgnoreCase("german")) {
            return "Nicht angemeldet";
        }
        return "Not logged in";
    }

    public static String getNotInstalled() {
        if(Main.language.equalsIgnoreCase("german")) {
            return "Nicht installiert";
        }
        return "Not installed";
    }
}