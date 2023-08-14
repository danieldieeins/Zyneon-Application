package live.nerotv.zyneon.app.backend.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import live.nerotv.Main;

import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

public class Config {

    private final ObjectMapper objectMapper;
    private final File jsonFile;
    private final String path;

    public Config(File file) {
        Main.debug("JSON folder(s) created: "+new File(file.getParent()).mkdirs());
        this.objectMapper = new ObjectMapper();
        this.jsonFile = file;
        if (!jsonFile.exists()) {
            createEmptyJsonFile();
        }
        this.path = URLDecoder.decode(file.getAbsolutePath(),StandardCharsets.UTF_8);
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