package live.nerotv.zyneon.app.backend.modpack;

import live.nerotv.Main;
import live.nerotv.zyneon.app.backend.utils.Config;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

public class Modpack {

    private URL url;
    private Config config;
    private String name;
    private String version;
    private String minecraftVersion;
    private String id;
    private Path path;

    public Modpack(String fileDownload) {
        try {
            url = new URL(fileDownload);
            InputStream inputStream = new BufferedInputStream(url.openStream());
            String uuid = UUID.randomUUID().toString();
            File cfg = new File(Main.getDirectoryPath()+"temp/"+uuid+".json");
            new File(cfg.getParent()).mkdirs();
            FileOutputStream outputStream = new FileOutputStream(cfg);
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            inputStream.close();
            outputStream.close();

            Config temp = new Config(cfg);
            if(temp.get("modpack.instance")!=null) {
                String path = Main.getDirectoryPath()+temp.get("modpack.instance")+"zyneonInstance.json";
                new File(new File(path).getParent()).mkdirs();
                Path source = Paths.get(Main.getDirectoryPath()+"temp/"+uuid+".json");
                Path destination = Paths.get(path);
                Files.copy(source, destination, StandardCopyOption.REPLACE_EXISTING);
                cfg.delete();
                config = new Config(new File(path));
                url = new URL((String)config.get("modpack.download"));
                name = (String)config.get("modpack.name");
                version = (String)config.get("modpack.version");
                minecraftVersion = (String)config.get("modpack.minecraft");
                id = (String)config.get("modpack.id");
                this.path = Path.of(Main.getDirectoryPath()+config.get("modpack.instance"));
                if(config==null||url==null||name==null||version==null||minecraftVersion==null||id==null) {
                    throw new NullPointerException("Modpack file doesn't contain all values");
                }
                if(config.get("modpack.installed")==null) {
                    update();
                } else if(((String)config.get("modpack.installed")).equalsIgnoreCase("false")) {
                    update();
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean unzipPack() {
        try (ZipArchiveInputStream zipInput = new ZipArchiveInputStream(new FileInputStream(path+"/pack.zip"))) {
            ArchiveEntry entry;
            while ((entry = zipInput.getNextEntry()) != null) {
                Path outputPath = Paths.get(path.toString(), entry.getName());
                if (entry.isDirectory()) {
                    outputPath.toFile().mkdirs();
                } else {
                    try (FileOutputStream outputStream = new FileOutputStream(outputPath.toFile())) {
                        byte[] buffer = new byte[1024];
                        int bytesRead;
                        while ((bytesRead = zipInput.read(buffer)) != -1) {
                            outputStream.write(buffer, 0, bytesRead);
                        }
                    }
                }
            }
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public Config getConfig() {
        return config;
    }

    public boolean update() {
        System.out.println("trying to download: "+url);
        try {
            InputStream inputStream = new BufferedInputStream(url.openStream());
            FileOutputStream outputStream = new FileOutputStream(path+"/pack.zip");
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            inputStream.close();
            outputStream.close();
            return unzipPack();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Path getPath() {
        return path;
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
        path = null;
        System.gc();
    }
}