package com.zyneonstudios;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Updater {

    private static ZyneonSplash splash;

    public static void main(String[] a) {
        splash = new ZyneonSplash();
        splash.setVisible(true);

        new Updater(initArgs(a));
    }

    private static boolean initArgs(String[] args) {
        for(String a:args) {
            if(a.equals("--experimental")) {
                return true;
            }
        }
        return false;
    }

    public Updater(boolean experimental) {
        System.out.println("Started updater...");
        try {
            File config = new File(getDirectoryPath() + "config.json");
            String newest = "0";
            String installedVersion = "-1";
            System.out.println("Getting latest version...");
            if(experimental) {
                try {












                    throw new Exception("wip");

                    /*TODO*/













                } catch (Exception e) {
                    System.err.println("An error occurred: "+e);
                    System.out.println(" ");
                    System.out.println("Try again or start without the --experimental flag.");
                    System.exit(-1);
                    throw new RuntimeException(e);
                }
            } else {
                InputStream inputStream = new BufferedInputStream(new URL("https://raw.githubusercontent.com/danieldieeins/ZyneonApplicationContent/main/l/application.json").openStream());
                FileOutputStream outputStream = new FileOutputStream(getDirectoryPath() + "updater.json");
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
                inputStream.close();
                outputStream.close();
                System.out.println("Checking installed version...");
                File currentFile = new File(getDirectoryPath() + "updater.json");

                Config current = new Config(currentFile);
                Config installed = new Config(config);


                if (current.get("updater.version") != null) {
                    newest = (String) current.get("updater.version");
                }
                if (installed.get("updater.version") != null) {
                    installedVersion = (String) installed.get("updater.version");
                }

                if (!installedVersion.equals(newest)) {
                    System.out.println("Updating...");
                    downloadApp(current);
                    installed.set("updater.version", newest);
                } else {
                    System.out.println("Application is up to date!");
                }
            }
            System.out.println("Launching...");
            splash.setVisible(false);
            launch();
        } catch (Exception e) {
            splash.setVisible(false);
            throw new RuntimeException(e);
        }
        splash.setVisible(false);
        System.exit(0);
    }

    private void downloadApp(Config json) {
        System.out.println("Downloading latest version...");
        try {
            InputStream inputStream = new BufferedInputStream(new URL((String) json.get("updater.download")).openStream());
            FileOutputStream outputStream = new FileOutputStream(getDirectoryPath() + "application.jar");
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            inputStream.close();
            outputStream.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void launch() {
        try {
            ProcessBuilder processBuilder;
            if(new File(getDirectoryPath()+"test.jar").exists()) {
                processBuilder = new ProcessBuilder("java", "-jar", "--add-opens", "java.desktop/sun.awt=ALL-UNNAMED", "--add-opens", "java.desktop/sun.lwawt=ALL-UNNAMED", "--add-opens", "java.desktop/sun.lwawt.macosx=ALL-UNNAMED", getDirectoryPath() + "test.jar", "application", "--add-opens java.desktop/sun.awt=ALL-UNNAMED --add-opens java.desktop/sun.lwawt=ALL-UNNAMED --add-opens java.desktop/sun.lwawt.macosx=ALL-UNNAMED");
            } else {
                processBuilder = new ProcessBuilder("java", "-jar", "--add-opens", "java.desktop/sun.awt=ALL-UNNAMED", "--add-opens", "java.desktop/sun.lwawt=ALL-UNNAMED", "--add-opens", "java.desktop/sun.lwawt.macosx=ALL-UNNAMED", getDirectoryPath() + "application.jar", "application", "--add-opens java.desktop/sun.awt=ALL-UNNAMED --add-opens java.desktop/sun.lwawt=ALL-UNNAMED --add-opens java.desktop/sun.lwawt.macosx=ALL-UNNAMED");
            }
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();
            InputStream inputStream = process.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
            int exitCode = process.waitFor();
            System.out.println(exitCode);
            System.exit(0);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String path;

    public String getDirectoryPath() {
        if (path == null) {
            String folderName = "Zyneon/Application";
            String appData;
            String os = System.getProperty("os.name").toLowerCase();
            if (os.contains("win")) {
                appData = System.getenv("LOCALAPPDATA");
            } else if (os.contains("mac")) {
                appData = System.getProperty("user.home") + "/Library/Application Support";
            } else {
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

    private class Config {

        private final ObjectMapper objectMapper;
        private final File jsonFile;
        private final String path;

        public Config(File file) {
            new File(file.getParent()).mkdirs();
            this.objectMapper = new ObjectMapper();
            this.jsonFile = file;
            if (!jsonFile.exists()) {
                createEmptyJsonFile();
            }
            this.path = URLDecoder.decode(file.getAbsolutePath(), StandardCharsets.UTF_8);
        }

        private void createEmptyJsonFile() {
            try {
                JsonNode rootNode = JsonNodeFactory.instance.objectNode();
                objectMapper.writeValue(jsonFile, rootNode);
            } catch (IOException e) {
                throw new RuntimeException("Fehler beim Erstellen der Konfigurationsdatei", e);
            }
        }

        public File getJsonFile() {
            return jsonFile;
        }

        public String getPath() {
            return path;
        }

        public ObjectMapper getObjectMapper() {
            return objectMapper;
        }

        public void set(String path, Object value) {
            try {
                JsonNode rootNode = objectMapper.readTree(jsonFile);
                String[] parts = path.split("\\.");
                ObjectNode currentNode = (ObjectNode) rootNode;

                for (int i = 0; i < parts.length - 1; i++) {
                    if (!currentNode.has(parts[i]) || !currentNode.get(parts[i]).isObject()) {
                        currentNode.putObject(parts[i]);
                    }
                    currentNode = (ObjectNode) currentNode.get(parts[i]);
                }

                JsonNode valueNode = objectMapper.valueToTree(value);
                currentNode.set(parts[parts.length - 1], valueNode);

                objectMapper.writeValue(jsonFile, rootNode);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public Object get(String path) {
            try {
                JsonNode rootNode = objectMapper.readTree(jsonFile);
                String[] parts = path.split("\\.");
                JsonNode currentNode = rootNode;

                for (String part : parts) {
                    if (!currentNode.has(part)) {
                        return null;
                    }
                    currentNode = currentNode.get(part);
                }

                return objectMapper.treeToValue(currentNode, Object.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        public void delete(String path) {
            try {
                JsonNode rootNode = objectMapper.readTree(jsonFile);
                String[] parts = path.split("\\.");
                ObjectNode currentNode = (ObjectNode) rootNode;

                for (int i = 0; i < parts.length - 1; i++) {
                    if (!currentNode.has(parts[i]) || !currentNode.get(parts[i]).isObject()) {
                        return;
                    }
                    currentNode = (ObjectNode) currentNode.get(parts[i]);
                }

                currentNode.remove(parts[parts.length - 1]);

                objectMapper.writeValue(jsonFile, rootNode);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static class ZyneonSplash extends JWindow {

        public ZyneonSplash() {
            super();
            try {
                setBackground(new Color(0, 0, 0, 0));
                setSize(400, 400);
                setLocationRelativeTo(null);
                JLabel image;
                image = new JLabel(new ImageIcon(ImageIO.read(getClass().getResourceAsStream("/logo.png")).getScaledInstance(getWidth(), getHeight(), Image.SCALE_SMOOTH)));
                getContentPane().add(image);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}