package live.nerotv.zyneon.app.application.backend.integrations;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import live.nerotv.Main;
import live.nerotv.shademebaby.frame.NWebFrame;

import java.awt.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ModrinthIntegration {

    public static void main(String[] a) {
        JsonObject search = searchMods(Modloader.FABRIC,"1.20.1",0,50);
        JsonArray results = search.getAsJsonArray("hits");
        System.out.println(search);
        System.out.println(results);
        NWebFrame f = new NWebFrame("C:\\Users\\nerotvlive\\Desktop\\Application Beta 7\\search.html", Main.getDirectoryPath()+"libs/jcef");
        f.setSize(new Dimension(1280,720));
        f.setLocationRelativeTo(null);
        f.setVisible(true);
        for (int i = 0; i < results.size(); i++) {
            JsonObject item = results.get(i).getAsJsonObject();
            String id = item.get("project_id").getAsString();
            String slug = item.get("slug").getAsString();
            String author = item.get("author").getAsString();
            String title = item.get("title").getAsString();
            String description = item.get("description").getAsString();
            System.out.println("===============================");
            System.out.println("ID: "+id+" (slug: "+slug+")");
            System.out.println("Title: "+title+" by "+author);
            System.out.println("Description: "+description);
        }
    }

    private static JsonObject getObject(String url) {
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod("GET");
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();
                return JsonParser.parseString(response.toString()).getAsJsonObject();
            }
            connection.disconnect();
        } catch (Exception ignore) {}
        return null;
    }

    public static JsonObject searchMods(String query, Modloader loader, String version, int offset, int limit) {
        try {
            return getObject("https://api.modrinth.com/v2/search?query="+query.toLowerCase()+"&=facets=[[%22categories:"+loader.toString().toLowerCase()+"%22],[%22versions:"+version+"%22],[%22project_type:mod%22]&offset="+offset+"&limit="+limit);
        } catch (Exception ignore) {}
        return null;
    }

    public static JsonObject searchMods(Modloader loader, String version, int offset, int limit) {
        try {
            return getObject("https://api.modrinth.com/v2/search?=facets=[[%22categories:"+loader.toString().toLowerCase()+"%22],[%22versions:"+version+"%22],[%22project_type:mod%22]&offset="+offset+"&limit="+limit);
        } catch (Exception ignore) {}
        return null;
    }

    public enum Modloader {
        FORGE,
        FABRIC
    }
}