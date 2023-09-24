package live.nerotv.zyneon.app.application.backend.instance;

import live.nerotv.Main;
import live.nerotv.shademebaby.file.Config;
import live.nerotv.shademebaby.file.FileUtils;

import java.io.File;
import java.util.UUID;

public class VanillaInstance implements Instance {

    private Config json;
    private String path;
    private String id;
    private String name;
    private String version;
    private String minecraftVersion;

    public VanillaInstance(Config json) {
        this.json = json;
        id = json.getString("modpack.id");
        name = json.getString("modpack.name");
        version = json.getString("modpack.version");
        minecraftVersion = json.getString("modpack.minecraft");
        path = Main.getDirectoryPath() + json.getString("modpack.instance");
    }

    @Override
    public boolean checkVersion() {
        try {
            String url = "https://raw.githubusercontent.com/danieldieeins/ZyneonApplicationContent/main/m/" + id + ".json";
            new File(Main.getDirectoryPath()+"temp/").mkdirs();
            Config json = new Config(FileUtils.downloadFile(url, Main.getDirectoryPath() + "temp/" + UUID.randomUUID() + ".json"));
            String version = json.getString("modpack.version");
            String installed = this.json.getString("modpack.version");
            json.getJsonFile().delete();
            if (!version.equals(installed)) {
                return false;
            }
        } catch (Exception ignore) {}
        return true;
    }

    @Override
    public boolean update() {
        System.out.println("TRYING TO UPDATE INSTANCE " + name + " (" + id + ")...");
        try {
            if(new File(path+"mods/").exists()) {
                new File(path+"mods/").delete();
            }
            FileUtils.downloadFile(json.getString("modpack.download"), path + "/pack.zip");
            FileUtils.unzipFile(path + "/pack.zip", path);
            String url = "https://raw.githubusercontent.com/danieldieeins/ZyneonApplicationContent/main/m/" + id + ".json";
            json = new Config(FileUtils.downloadFile(url, path + "/zyneonInstance.json"));
            minecraftVersion = json.getString("modpack.minecraft");
            version = json.getString("modpack.version");
            name = json.getString("modpack.name");
        } catch (Exception e) {
            System.out.println("NOT UPDATED!");
            return false;
        }
        System.out.println("SUCCESSFULLY UPDATED!");
        return true;
    }

    @Override
    public Config getJSON() {
        return json;
    }

    @Override
    public String getPath() {
        return path;
    }

    @Override
    public String getID() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getVersion() {
        return version;
    }

    @Override
    public String getMinecraftVersion() {
        return minecraftVersion;
    }

    public void unload() {
        id = null;
        path = null;
        name = null;
        json = null;
        version = null;
        minecraftVersion = null;
        System.gc();
    }
}