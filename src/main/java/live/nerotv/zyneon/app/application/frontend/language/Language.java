package live.nerotv.zyneon.app.application.frontend.language;

import live.nerotv.Main;

public class Language {

    public static void syncLanguage() {
        if(Main.language.equalsIgnoreCase("german")) {
            German.syncLanguage();
            return;
        }
        English.syncLanguage();
    }

    public static String getNotLoggedIn() {
        if(Main.language.equalsIgnoreCase("german")) {
            return "Nicht angemeldet";
        }
        return "Not logged in";
    }
}