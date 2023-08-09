package live.nerotv.zyneon.app.backend.modpack;

import java.net.URL;

public class Modpack {

    private URL url;
    private String name;
    private String version;
    private String minecraftVersion;
    private String id;

    public Modpack(URL fileDownload) {
        url = fileDownload;
        //falls die URL zur JSON Datei erfolgreich runterladen konnte, die Daten der Datei entnehmen, wenn nicht Folgendes:
        name = "Keine Verbindung";
        version = "Unbekannt";
        minecraftVersion = "Unbekannt";
        id = "Unbekannt";
    }

    public void update(URL fileDownload) {
        url = fileDownload;
        //falls die URL zur JSON Datei erfolgreich runterladen konnte, die Daten der Datei entnehmen, wenn nicht Folgendes:
        name = "Keine Verbindung";
        version = "Unbekannt";
        minecraftVersion = "Unbekannt";
        id = "Unbekannt";
    }

    public String getName() {
        return name;
    }

    public String getVersion() {
        return version;
    }

    public String getID() {
        return id;
    }

    public String getMinecraftVersion() {
        return minecraftVersion;
    }

    public void unload() {
        url = null;
        name = null;
        version = null;
        minecraftVersion = null;
        id = null;
        System.gc();
    }
}