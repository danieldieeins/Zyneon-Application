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
    private String latest = Main.version;
    private boolean started = false;
    public int i = 7;
    private int u = 118;

    public Runner(Application application) {
        executor = Executors.newScheduledThreadPool(1);
        executor.scheduleAtFixedRate(() -> CompletableFuture.runAsync(() -> run(application)), 0, 30, TimeUnit.SECONDS);
    }

    public ScheduledExecutorService getExecutor() {
        return executor;
    }

    private void run(Application application) {
        i++; u++;
        if(!started) {
            started = true;
            return;
        }
        try {
            if(u>119) {
                u=0;
                Main.getLogger().debug("[RUNNER] Checking for Updates...");
                JsonObject json = new Gson().fromJson(GsonUtil.getFromURL("https://raw.githubusercontent.com/danieldieeins/ZyneonApplicationContent/main/l/application.json"), JsonObject.class);
                Main.getLogger().debug("[RUNNER] Parsed JSON Data...");
                String v = json.get("updater").getAsJsonObject().get("version").getAsString();
                Main.getLogger().debug("[RUNNER] Latest version: " + v + "...");
                Main.getLogger().debug("[RUNNER] Current version: " + version + "...");
                if (!v.equals(version)) {
                    Main.getLogger().debug("[RUNNER] Saving new information...");
                    latest = v;
                    Main.getLogger().debug("[RUNNER] Sending notification...");
                    Application.getFrame().sendNotification("Update available!", "Version " + latest + " has been released!", "<a onclick=\"callJavaMethod('button.exit');\" class='button'>Install</a><a onclick=\"callJavaMethod('button.online');\" class='button'>Dynamic update</a>", false);
                    Main.getLogger().debug("[RUNNER] The application is not up to date!");
                }
            }
            if(i>9) {
                i=0;
                if(Application.online) {
                    Application.getFrame().sendNotification("Dynamic Update is activated!", "You are currently using the dynamic update, which means that the user interface<keeps itself up to date<br>but it can also lead to problems if the backend and frontend versions become too different.", "<a onclick=\"callJavaMethod('button.exit');\" class='button'>Install</a><a onclick=\"callJavaMethod('button.online');\" class='button'>Dynamic update</a>", false);
                }
            }
        } catch (Exception ignore) {}
    }

    private void checkVersion() {

    }
}