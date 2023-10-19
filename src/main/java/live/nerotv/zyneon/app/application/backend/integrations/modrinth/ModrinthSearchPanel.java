package live.nerotv.zyneon.app.application.backend.integrations.modrinth;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import live.nerotv.zyneon.app.application.Application;
import live.nerotv.zyneon.app.application.backend.instance.Instance;
import org.cef.CefClient;
import org.cef.browser.CefBrowser;
import org.cef.handler.CefLoadHandler;
import org.cef.handler.CefLoadHandlerAdapter;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class ModrinthSearchPanel {

    private final Instance instance;
    private final JFrame frame;
    private final CefBrowser browser;
    private final Component ui;
    private final CefClient client;
    private final String query;

    public ModrinthSearchPanel(String query, Instance instance, ModrinthIntegration.Modloader modloader) {
        this.query = query;
        this.instance = instance;
        frame = new JFrame("%modrinth%");
        client = Application.getFrame().getApp().createClient();
        browser = client.createBrowser("https://danieldieeins.github.io/ZyneonApplicationContent/h/search/search-modrinth.html",false,false);
        ui = browser.getUIComponent();
        try {
            frame.setIconImage(ImageIO.read(getClass().getResource("/logo.png")).getScaledInstance(32,32,Image.SCALE_SMOOTH));
        } catch (IOException ignore) {}
        frame.getContentPane().add(ui,BorderLayout.CENTER);
        frame.setSize(1280,720);
        CefLoadHandler loadHandler = new CefLoadHandlerAdapter() {
            @Override
            public void onLoadingStateChange(CefBrowser browser, boolean isLoading, boolean canGoBack, boolean canGoForward) {
                if (!isLoading) {
                    init(ModrinthIntegration.searchMods(query,modloader,instance.getVersion(),0,100));
                }
            }
        };
        client.addLoadHandler(loadHandler);
    }

    public String getQuery() {
        return query;
    }

    public CefBrowser getBrowser() {
        return browser;
    }

    public CefClient getClient() {
        return client;
    }

    public Component getUI() {
        return ui;
    }

    public Instance getInstance() {
        return instance;
    }

    public JFrame getFrame() {
        return frame;
    }

    public void init(JsonObject search) {
        JsonArray results = search.getAsJsonArray("hits");
        for (int i = 0; i < results.size(); i++) {
            JsonObject item = results.get(i).getAsJsonObject();
            String id = item.get("project_id").getAsString();
            String slug = item.get("slug").getAsString();
            String modurl = "https://modrinth.com/mod/"+slug;
            String author = item.get("author").getAsString();
            String title = item.get("title").getAsString();
            String description = item.get("description").getAsString();
            String png = item.get("icon_url").getAsString();
            String js = "syncModCard(\""+title+"\",\""+id+"\",\""+description+"\",\""+author+"\",\""+png+"\",\"javascript:callJavaMethod('button.show.modrinth."+slug+"')\",'1.20.1');";
            System.out.println(js);
            browser.executeJavaScript(js,browser.getURL(),1);
            browser.setZoomLevel(-2);
        }
    }

    public void show() {
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public void hide() {
        frame.setVisible(false);
    }
}