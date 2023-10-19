package live.nerotv.zyneon.app.application.backend.integrations.modrinth;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import live.nerotv.zyneon.app.application.backend.utils.frame.ZyneonWebFrame;
import org.cef.browser.CefBrowser;
import org.cef.handler.CefLoadHandler;
import org.cef.handler.CefLoadHandlerAdapter;

import java.awt.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ModrinthIntegration {

    public static void main(String[] a) {
        JsonObject search = searchMods("Blockbuster",Modloader.FORGE,"1.12.2",0,100);
        JsonArray results = search.getAsJsonArray("hits");
        System.out.println(search);
        System.out.println(results);
        ZyneonWebFrame f = new ZyneonWebFrame("C:\\Users\\nerotvlive\\Desktop\\A\\search-modrinth.html");
        CefLoadHandler loadHandler = new CefLoadHandlerAdapter() {
            @Override
            public void onLoadingStateChange(CefBrowser browser, boolean isLoading, boolean canGoBack, boolean canGoForward) {
                if (!isLoading) {
                    for (int i = 0; i < results.size(); i++) {
                        JsonObject item = results.get(i).getAsJsonObject();
                        String id = item.get("project_id").getAsString();
                        String slug = item.get("slug").getAsString();
                        String modurl = "https://modrinth.com/mod/"+slug;
                        String author = item.get("author").getAsString();
                        String title = item.get("title").getAsString();
                        String description = item.get("description").getAsString();
                        String png = item.get("icon_url").getAsString();
                        System.out.println("===============================");
                        System.out.println("ID: "+id+" (slug: "+slug+")");
                        System.out.println("Title: "+title+" by "+author);
                        System.out.println("Description: "+description);
                        System.out.println(modurl);
                        String js = "syncModCard(\""+title+"\",\""+id+"\",\""+description+"\",\""+author+"\",\""+png+"\",\"javascript:callJavaMethod('button.show.modrinth."+slug+"')\",'1.20.1');";
                        System.out.println(js);
                        f.getBrowser().executeJavaScript(js,f.getBrowser().getURL(),1);
                        f.getBrowser().setZoomLevel(-2);
                    }

                }
            }
        };
        f.getClient().addLoadHandler(loadHandler);
        f.setSize(new Dimension(1280,720));
        f.setLocationRelativeTo(null);
        f.setVisible(true);
    }

    private static JsonObject getObject(String url) {
        System.out.println(url);
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
            return getObject("https://api.modrinth.com/v2/search?query="+query.toLowerCase()+"&facets=[[%22categories:"+loader.toString().toLowerCase()+"%22],[%22versions:"+version+"%22],[%22project_type:mod%22]]&offset="+offset+"&limit="+limit);
        } catch (Exception ignore) {}
        return null;
    }

    public static JsonObject searchMods(Modloader loader, String version, int offset, int limit) {
        try {
            return getObject("https://api.modrinth.com/v2/search?facets=[[%22categories:"+loader.toString().toLowerCase()+"%22],[%22versions:"+version+"%22],[%22project_type:mod%22]]&offset="+offset+"&limit="+limit);
        } catch (Exception ignore) {}
        return null;
    }

    public enum Modloader {
        FORGE,
        FABRIC
    }
}