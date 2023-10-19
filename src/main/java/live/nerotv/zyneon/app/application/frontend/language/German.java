package live.nerotv.zyneon.app.application.frontend.language;

import live.nerotv.Main;

import static live.nerotv.zyneon.app.application.frontend.language.Language.sync;

public class German {

    public static void syncLanguage() {
        //Startseite (start/news)
        sync("start","Start/News");
        sync("instances_button","Instanzen");
        sync("settings","Einstellungen");
        sync("profile","Profilverwaltung");
        sync("zyneonplus_text","Zyneon+ ist der Weg um ein optimiertes Minecraft-Abenteuer zu genießen!");
        sync("zyneonplus_button","Ansehen");
        sync("news_value","Neuigkeiten");
        sync("news_loading","Ladevorgang...");
        sync("news_loadingtext","Versuche die News abzurufen...");

        //Instanzübersicht (instance view)
        sync("create","Neu");
        sync("instances_create","Instanz importieren");
        sync("instances_new","Instance Creator");

        //Zyneon+ (zyneonplus instance)
        sync("play","Spielen");
        sync("zyneonplus_change_version","Version wählen");
        sync("zyneonplus_versiontext","Klicke auf Version wählen um Zyneon+ für andere Minecraft-Versionen zu spielen");
        sync("worlds","Welten");
        sync("description","Beschreibung");
        sync("zyneonplus_slogan","Erlebe Minecraft wie noch nie zuvor!");
        sync("zyneonplus_latest_versiontext","Klicke auf Version wählen um Zyneon+ für andere Minecraft-Versionen zu spielen");
        sync("zyneonplus_description", "Dieses Pack bietet dir die perfekte Optimierung und die besten Features.\\u003cbr\\u003eEgal ob du mit deinen Freunden spielen willst, oder einfach allein - Zyneon+ ist für dich da. \\u003cbr\\u003e\\u003cbr\\u003eLade deine Freunde online in deine eigentlich-Einzelspieler-Welt ein, ganz ohne Server.\\u003cbr\\u003eSpiele mit den aktuellsten Shadern und; oder Resourcepacks. Ändere deinen Skin - und das ingame. \\u003cbr\\u003e\\u003cbr\\u003eDie Möglichkeiten sind grenzenlos - erlebe dein Abenteuer und das mit der besten Performance!");

        //Zyneon+ Versionen (zyneonplus versions)
        sync("zyneonplus_select_version","Wähle eine Zyneon+ Version aus");
        sync("zyneonplus_dynamic","Wähle \"Latest\" um immer auf der neusten Version zu sein!");
        sync("dynamic","Latest");

        //Project'Z (projectz)
        sync("projectz_slogan","Das neue Zyneon Studios Projekt!");
        //sync("projectz_description","Die Moderne erwartet dich in einem Tech-basierten Modpack!");
        sync("projectz_button","Ansehen");

        //Instanzseitze (instance)
        sync("official","Offiziell");

        //Einstellungen (settings)
        sync("setting_starttab_title","Start-Tab:");
        sync("setting_starttab_description","Wähle den Tab aus, welcher beim Starten der App geöffnet werden soll.");
        sync("setting_memory_title","Globale Arbeitsspeicher-Einstellungen:");
        sync("setting_memory_description","Setze den Standard-RAM Wert. Dieser Wert wird von Modpack spezifischen RAM-Einstellungen überschrieben.");
        sync("setting_memory_button","Arbeitsspeicher einstellen");
        sync("setting_language_title","Sprache:");
        sync("setting_language_description","Wähle die Application-Sprache aus, indem du auf den \"Deutsch\"-Knopf drückst.");
        if(Main.config.getString("settings.language").equalsIgnoreCase("auto")) {
            sync("setting_language_button", "Automatisch (German)");
        } else {
            sync("setting_language_button", "Deutsch (German)");
        }
        if(Main.starttab.equalsIgnoreCase("start")) {
            sync("setting_starttab_button", "Startseite");
        } else {
            sync("setting_starttab_button", "Instanzenübersicht");
        }

        //Profilverwaltung (profiles)
        sync("add_account","Account hinzufügen");
        sync("logoutall","Alle Accounts abmelden");
        sync("profiles_main","Ausgewähltes Profil:");
        sync("change_username","Spielernamen ändern");
        sync("change_skin","Skin ändern");
        sync("main_account","Ansehen");
        sync("logout","Abmelden");
        sync("select","Auswählen");

        //modals
        sync("installing","Herunterladen...");
        sync("installing_text","Die Application aktualisiert die Instanz.<br>Vielleicht reagiert sie kurz nicht, aber das ist völlig normal - einfach kurz warten.<br><br>Du kannst diese Meldung schließen, wenn du magst.");
        sync("starting","Starte...");
        sync("starting_text","Die Instanz wird gestartet.<br>Es kann sein, dass noch Aktualisierungen abgeschlossen werden müssen - dann reagiert die Application kurz nicht, aber das ist völlig normal - einfach kurz warten.<br><br>Du kannst diese Meldung schließen, wenn du magst.");
        sync("close","Schliessen");
        sync("close","Schliessen");

        //Fehlende Übersetzung auf Englisch zurücksetzen (Set fallback language to english)
        English.syncLanguage();
    }
}