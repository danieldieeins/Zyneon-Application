package com.zyneonstudios.application.utils.backend;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.zyneonstudios.Main;
import com.zyneonstudios.application.Application;
import live.nerotv.shademebaby.utils.GsonUtil;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Runner {

    private final ScheduledExecutorService executor;
    private final String version = Main.version;
    private boolean started = false;
    public int i = 7;
    private int u = 118;

    private String motd_id = "";

    public Runner() {
        executor = Executors.newScheduledThreadPool(1);
        executor.scheduleAtFixedRate(() -> CompletableFuture.runAsync(() -> run()), 0, 30, TimeUnit.SECONDS);
    }

    public ScheduledExecutorService getExecutor() {
        return executor;
    }

    public void run() {
        i++; u++;
        if(!started) {
            started = true;
            return;
        }
        try {
            JsonObject json = new Gson().fromJson(GsonUtil.getFromURL("https://raw.githubusercontent.com/danieldieeins/ZyneonApplicationContent/main/l/application.json"), JsonObject.class);
            if(u>119) {
                u = 0;
                if(!Main.isTest()) {
                    Main.getLogger().debug("[RUNNER] Checking for Updates...");
                    Main.getLogger().debug("[RUNNER] Parsed JSON Data...");
                    String v = json.get("updater").getAsJsonObject().get("version").getAsString();
                    Main.getLogger().debug("[RUNNER] Latest version: " + v + "...");
                    Main.getLogger().debug("[RUNNER] Current version: " + version + "...");
                    if (!v.equals(version)) {
                        Main.getLogger().debug("[RUNNER] Saving new information...");
                        Main.getLogger().debug("[RUNNER] Sending notification...");
                        Application.getFrame().sendNotification("Update available!", "Version " + v + " has been released!", "<a onclick=\"callJavaMethod('button.exit');\" class='button'>Install</a><a onclick=\"callJavaMethod('button.online');\" class='button'>Dynamic update</a>", v, true);
                        Main.getLogger().debug("[RUNNER] The application is not up to date!");
                    }
                }
            }
            if(i>9) {
                i=0;
                if(Application.online) {
                    Application.getFrame().sendNotification("Dynamic Update is activated!", "You are currently using the dynamic update, which means that the user interface keeps itself up to date \n <br> but it can also lead to problems if the backend and frontend versions become too different.", "<a onclick=\"callJavaMethod('button.exit');\" class='button'>Install</a><a onclick=\"callJavaMethod('button.online');\" class='button'>Dynamic update</a>","dynamicUpdate",true);
                }
            }
            if(!json.get("message").getAsJsonObject().get("id").getAsString().equals(motd_id)) {
                json = json.getAsJsonObject("message");
                motd_id = json.get("id").getAsString();
                Application.getFrame().sendNotification(json.get("title").getAsString(),json.get("text").getAsString(),json.get("actions").getAsString(),motd_id,true);
            }
        } catch (Exception ignore) {}
    }

    private void checkVersion() {

    }
}