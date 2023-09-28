package live.nerotv;

import live.nerotv.shademebaby.ShadeMeBaby;
import live.nerotv.shademebaby.file.Config;
import live.nerotv.shademebaby.file.FileUtils;
import live.nerotv.shademebaby.logger.Logger;
import live.nerotv.zyneon.app.application.Application;

import java.io.File;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {

    private static String zyverse;
    private static String path;
    public static Config config;
    public static Config language;
    private static Logger logger;
    public static String v;

    public static void main(String[] args) {
        v = "PB7";
        if(!new File(getDirectoryPath()+"libs/zyneon/"+v+"/index.html").exists()) {
            new File(getDirectoryPath()+"libs/zyneon/").mkdirs();
            FileUtils.downloadFile("https://github.com/danieldieeins/ZyneonApplicationContent/raw/main/h/" + v + "/content.zip", getDirectoryPath() + "libs/zyneon/" + v + ".zip");
            FileUtils.unzipFile(getDirectoryPath() + "libs/zyneon/" + v + ".zip", getDirectoryPath() + "libs/zyneon/" + v);
            new File(getDirectoryPath() + "libs/zyneon/" + v + ".zip").delete();
        }
        if(System.getProperty("user.language").equalsIgnoreCase("de")) {
            language = new Config(getDirectoryPath()+"libs/zyneon/"+v+"/de.json");
        } else {
            language = new Config(getDirectoryPath()+"libs/zyneon/"+v+"/en.json");
        }
        config = new Config(new File(getDirectoryPath() + "config.json"));
        config.checkEntry("settings.memory.default", 1024);
        config.checkEntry("settings.logger.debug", false);
        logger = new Logger("ZyneonApplication");
        logger.setDebugEnabled(config.getBool("settings.logger.debug"));
        ShadeMeBaby.getLogger().setDebugEnabled(config.getBool("settings.logger.debug"));
        new Application().start();
    }

    public static Logger getLogger() {
        return logger;
    }

    public static String getDirectoryPath() {
        if (path == null) {
            String folderName = "Zyneon/Application";
            String appData;
            String os = System.getProperty("os.name").toLowerCase();
            if (os.contains("win")) {
                Main.os = "Windows";
                appData = System.getenv("LOCALAPPDATA");
            } else if (os.contains("mac")) {
                Main.os = "macOS";
                appData = System.getProperty("user.home") + "/Library/Application Support";
            } else {
                Main.os = "Unix";
                appData = System.getProperty("user.home") + "/.local/share";
            }
            Path folderPath = Paths.get(appData, folderName);
            try {
                Files.createDirectories(folderPath);
            } catch (Exception e) {
                e.printStackTrace();
            }
            path = folderPath + "/";
        }
        return URLDecoder.decode(path, StandardCharsets.UTF_8);
    }

    public static String os;

    public static String getZyversePath() {
        if (zyverse == null) {
            String folderName = "Zyneon/Zyverse";
            String appData;
            String os = System.getProperty("os.name").toLowerCase();
            if (os.contains("win")) {
                Main.os = "Windows";
                appData = System.getenv("LOCALAPPDATA");
            } else if (os.contains("mac")) {
                Main.os = "macOS";
                appData = System.getProperty("user.home") + "/Library/Application Support";
            } else {
                Main.os = "Unix";
                appData = System.getProperty("user.home") + "/.local/share";
            }
            Path folderPath = Paths.get(appData, folderName);
            try {
                Files.createDirectories(folderPath);
            } catch (Exception e) {
                e.printStackTrace();
            }
            zyverse = folderPath + "/";
        }
        return URLDecoder.decode(zyverse, StandardCharsets.UTF_8);
    }

    private static void initGerman() {
        language.checkEntry("zyneonplus.text", "Zyneon+ ist der Weg um ein optimiertes Minecraft-Abenteuer zu genießen!");
        language.checkEntry("zyneonplus.button", "Zyneon+ ansehen");
        language.checkEntry("zyneonplus.slogan", "Erlebe Minecraft, wie noch nie zuvor!");
        language.checkEntry("zyneonplus.description", "Dieses Pack bietet dir die perfekte Optimierung und die besten Features.<br>Egal ob du mit deinen Freunden spielen willst, oder einfach allein - Zyneon+ ist für dich da. <br><br>Lade deine Freunde online in deine eigentlich-Einzelspieler-Welt ein, ganz ohne Server.<br>Spiele mit den aktuellsten Shadern und; oder Resourcepacks. Ändere deinen Skin - und das ingame. <br><br>Die Möglichkeiten sind grenzenlos - erlebe dein Abenteuer und das mit der besten Performance!");
        language.checkEntry("zyneonplus.versiontext", "Klicke auf Version wählen um Zyneon+ für andere Minecraft-Versionen zu spielen");
        language.checkEntry("zyneonplus.change.version", "Version wählen");
        language.checkEntry("zyneonplus.select.version", "Zyneon+ Version wählen");
        language.checkEntry("account.notLoggedIn", "Nicht eingeloggt");
        language.checkEntry("news.value", "Neuigkeiten");
        language.checkEntry("starting.title", "Starte...");
        language.checkEntry("starting.text", "Die Instanz wird gestartet.<br><br>Es kann sein, dass die Anwendung kurz nicht reagiert.<br>Das ist aber völlig normal - keine Sorge!<br><br>Du kannst diese Meldung schließen, wenn du magst.");
        language.checkEntry("installing.title", "Installiere...");
        language.checkEntry("installing.text", "Die Instanz wird installiert.<br><br>Es kann sein, dass die Anwendung kurz nicht reagiert.<br>Das ist aber völlig normal - keine Sorge!<br><br>Du kannst diese Meldung schließen, wenn du magst.");
        language.checkEntry("news.value", "Neuigkeiten");
        language.checkEntry("profile", "Profil verwalten");
        language.checkEntry("play", "Spielen");
        language.checkEntry("close", "Schließen");
        language.checkEntry("worlds", "Welten");
        language.checkEntry("description", "Beschreibung");
        language.checkEntry("start", "Start/News");
        language.checkEntry("news.upcoming", "Kommende Features");
        language.checkEntry("news.instance.creator", "• Ein Instanz-Ersteller um eigene Fabric, Forge oder Vanilla Instanzen zu erstellen");
        language.checkEntry("news.instance.importer", "• Ein-Instanz Ex- und Importer, um Instanzen per ID zu teilen und zu empfangen");
        language.checkEntry("news.probably.more", "Und möglicherweise noch mehr...");
        language.checkEntry("news.pb7", "Die UI-Aktualisierung (Öffentliche Beta 7)");
        language.checkEntry("news.changelog.pb7", "28.09.2023: Die Application hat ein großes UI Update erhalten.<br>Zusätzlich wurden auch einige Funktionen hinzugefügt!<br><br>Hinzugefügt:<br>- Neues UI Design (v124)<br>- Die App passt sich nun automatisch der Sprache an, wenn dein PC auf Englisch ist, ist die App es auch, auf Deutsch ist die auf Deutsch.<br>- Zyneon+ 1.20.2<br>- Zyneon+ Versionsauswahl<br>- Speichern der zuletzt ausgewählten Zyneon+ Version<br>- Vorbereitung der Installationspfad-Verschiebung<br>- Vorbereitung auf die Implementierung des Instance Creators und des Instance Exporters sowie des Instance Importers<br>- Vorbereitung auf die Profilverwaltung<br>- Vorbereitung auf Public Beta 8<br><br>Entfernt:<br>- Argria II");
        language.checkEntry("news.pb6", "Öffentliche Beta 6");
        language.checkEntry("news.changelog.pb6", "26.09.2023: Die Application wurde auf die Public Beta 6 aktualisiert!<br>Dieses Update ist lediglich ein UI Update.<br><br>Hinzugefügt:<br>- Neue Zyneon+ Seite");
        language.checkEntry("news.pb5", "Öffentliche Beta 5");
        language.checkEntry("news.changelog.pb5", "25.09.2023: Die Application wurde auf die Public Beta 5 aktualisiert!<br>Diese Version hat einige unter-der-Haube Veränderungen, Bugfixes und folgende Features<br><br>Hinzugefügt:<br>- Automatisches Aktualisieren beim An- und Abmelden.<br>- Standard RAM-Einstellungen speichern (Bugfix)<br>- UI Update 122: Start/News aktualisiert");
        language.checkEntry("news.pb4", "Öffentliche Beta 4");
        language.checkEntry("news.changelog.pb4", "24.09.2023: Die Application wurde auf die Public Beta 4 aktualisiert!<br>Diese Version bringt eine Reihe von Backend Performance Updates und einige Bugfixes mit sich.<br><br>Hinzugefügt:<br>- Start/News Seite im Launcher mit diesen Informationen.<br>- RAM-Einstellungen (Bugfix)<br>- Zyneon Application Logo in der Taskleiste (Bugfix)<br>- Zyneon Application Logo in der Titelleiste (Bugfix)<br>- 1.16.5-Button für Zyneon+<br>- Vorbereitung auf die Instanzpfad-Verschiebung<br><br>Entfernt:<br>- Instanzen-Dropdown-Menü (Bug)");
        language.checkEntry("news.pb3", "Öffentliche Beta 3");
        language.checkEntry("news.changelog.pb3", "23.09.2023: Performance Update im Backend, keine direkten Neuerungen.");
        language.checkEntry("news.pb2", "Öffentliche Beta 2");
        language.checkEntry("news.changelog.pb2", "23.09.2023: Die Installer wurden aktualisiert!<br>Die Application wurde nun auf Java 21 aktualisiert, zudem wurden ältere Java Versionen hinzugefügt, um mehr Kompatibilität zu bieten.<br><br>Hinzugefügt:<br>- Java 21: für die Application und Minecraft Versionen über 1.16.5<br>- Java 11: für Minecraft Versionen 1.13, 1.13.1, 1.13.2, 1.14, 1.14.1, 1.14.2, 1.14.3, 1.14.4, 1.15, 1.15.1, 1.15.2, 1.16, 1.16.1, 1.16.2, 1.16.3, 1.16.4 und 1.16.5<br>- Java 8: für Minecraft Versionen unter 1.13<br><br>Entfernt:<br>- RAM-Einstellungen (Bug)");
        language.checkEntry("news.pb1", "Öffentliche Beta 1");
        language.checkEntry("news.changelog.pb1", "23.09.2023: Die Zyneon Application ist nun in der Public Beta!<br>Das heißt, dass das Repository auf GitHub nun öffentlich ist und es richtige Installer gibt.<br><br>Aktuell nicht unterstützte Plattformen:<br>- macOS");
    }
}