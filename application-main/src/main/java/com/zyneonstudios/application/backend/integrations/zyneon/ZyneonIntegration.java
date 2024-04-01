package com.zyneonstudios.application.backend.integrations.zyneon;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.zyneonstudios.Main;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Deprecated
public class ZyneonIntegration {

    @Deprecated
    public static HashMap<String, ArrayList<String>> getFromServer(String type) {
        String jsonUrl = "https://raw.githubusercontent.com/danieldieeins/ZyneonApplicationContent/main/m/index.json";
        try {
            String jsonString = getFromURL(jsonUrl);
            Gson gson = new Gson();
            JsonObject jsonObject = gson.fromJson(jsonString, JsonObject.class);
            HashMap<String, ArrayList<String>> result = new HashMap<>();
            JsonObject content = jsonObject.getAsJsonObject(type);
            Set<Map.Entry<String, JsonElement>> entrySet = content.entrySet();
            for (Map.Entry<String, JsonElement> entry : entrySet) {
                ArrayList<String> elements = new ArrayList<>();
                JsonArray jsonArray = entry.getValue().getAsJsonArray();
                for (JsonElement element : jsonArray) {
                    String stringValue = element.getAsString();
                    elements.add(stringValue);
                }
                result.put(entry.getKey(),elements);
            }
            return result;
        } catch (Exception e) {
            Main.getLogger().error("[ZYNEON] (INTEGRATION) Couldn't get search data: "+e.getMessage());
            return null;
        }
    }

    @Deprecated
    private static String getFromURL(String urlString) throws Exception {
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        reader.close();
        connection.disconnect();
        return response.toString();
    }

    @Deprecated
    public static String format(String in) {
        in = in
                .replace(".","%")
                .replace("_","%")
                .replace("/","%")
                .replace(" ","%")
                .replace("\"","%")
                .replace("\\","%");
        return in;
    }
}
