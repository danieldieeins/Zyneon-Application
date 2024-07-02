package com.zyneonstudios.application.main;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.zyneonstudios.application.download.Download;
import com.zyneonstudios.application.download.DownloadManager;
import com.zyneonstudios.application.frame.web.ApplicationFrame;
import live.nerotv.shademebaby.utils.GsonUtil;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ApplicationRunner {

    private ScheduledExecutorService executor;
    private boolean started = false;
    private int u = 119;
    private final NexusApplication app;

    private UUID downloading = null;

    private final String version = ApplicationConfig.getApplicationVersion();
    //TODO private final String motd_id = "";

    public ApplicationRunner(NexusApplication app) {
        this.app = app;
    }

    public ScheduledExecutorService getExecutor() {
        return executor;
    }

    public void start() {
        executor = Executors.newScheduledThreadPool(1);
        executor.scheduleAtFixedRate(() -> CompletableFuture.runAsync(this::run), 0, 1, TimeUnit.SECONDS);
    }

    protected void run() {
        if (!started) {
            started = true;
            return;
        }


        if (downloading != null) {
            Download download = app.getDownloadManager().getDownloads().get(downloading);
            if (download.isFinished()) {
                downloading = null;
            }
        } else {
            app.getDownloadManager().getDownloads().forEach((uuid, download) -> {
                if (download.getState().equals(DownloadManager.DownloadState.WAITING)) {
                    downloading = uuid;
                    download.start();
                    ((ApplicationFrame) app.getFrame()).executeJavaScript("window.location.href = 'downloads.html?reInit=false';");
                }
            });
        }


        if (((ApplicationFrame) app.getFrame()).getBrowser().getURL().contains("/downloads.html")) {
            app.getDownloadManager().getDownloads().forEach((uuid, download) -> {
                if (download.getState().equals(DownloadManager.DownloadState.WAITING)) {
                    String title = "setDownload(\"" + download.getName().replace("\"", "''") + "\",";
                    String state = "\"" + download.getState().toString().replace("\"", "''") + "\",";
                    String elapsedTime = "\"0 seconds\",";
                    String downloadSpeed = "\"0 mb/s\",";
                    String remainingTime = "\"\",";
                    String downloadSize = "\"\",";
                    String fileSize = "\"0 mb\",";
                    String path = "\"" + download.getPath().toString().replace("\"", "''") + "\",";
                    String url = "\"" + download.getUrl().toString().replace("\"", "''") + "\",";
                    String id = "\"" + download.getId().replace("\"", "''") + "\",";
                    String progress = "\"" + download.getPercentString() + "\",";
                    String percent = download.getPercent() + ");";
                    String command = title + state + elapsedTime + downloadSpeed + remainingTime + downloadSize + fileSize + path + url + id + progress + percent;
                    ((ApplicationFrame) app.getFrame()).executeJavaScript(command);
                } else if (download.getState().equals(DownloadManager.DownloadState.RUNNING)) {
                    String title = "setDownload(\"" + download.getName().replace("\"", "''") + "\",";
                    String state = "\"" + download.getState().toString().replace("\"", "''") + "\",";
                    String elapsedTime = "\"" + download.getElapsedTime().getSeconds() + " seconds\",";
                    String downloadSpeed = "\"" + (int) download.getSpeedMbps() + " mb/s\",";
                    String remainingTime = "\"" + download.getEstimatedRemainingTime().getSeconds() + " seconds\",";
                    String downloadSize = "\"" + (download.getFileSize() / 1000) / 1000 + " mb\",";
                    String fileSize = "\"" + (download.getLastBytesRead() / 1000) / 1000 + " mb\",";
                    String path = "\"" + download.getPath().toString().replace("\"", "''") + "\",";
                    String url = "\"" + download.getUrl().toString().replace("\"", "''") + "\",";
                    String id = "\"" + download.getId().replace("\"", "''") + "\",";
                    String progress = "\"" + download.getPercentString() + "\",";
                    String percent = download.getPercent() + ");";
                    String command = title + state + elapsedTime + downloadSpeed + remainingTime + downloadSize + fileSize + path + url + id + progress + percent;
                    ((ApplicationFrame) app.getFrame()).executeJavaScript(command);
                } else if (download.getState().equals(DownloadManager.DownloadState.FINISHED) || download.getState().equals(DownloadManager.DownloadState.FAILED)) {
                    String title = "setDownload(\"" + download.getName().replace("\"", "''") + "\",";
                    String state = "\"" + download.getState().toString().replace("\"", "''") + "\",";
                    String elapsedTime = "\"" + download.getElapsedTime().getSeconds() + " seconds\",";
                    String downloadSpeed = "\"0 mb/s\",";
                    String remainingTime = "\"" + download.getEstimatedRemainingTime().getSeconds() + " seconds\",";
                    String downloadSize = "\"" + (download.getFileSize() / 1000) / 1000 + " mb\",";
                    String fileSize = downloadSize;
                    String path = "\"" + download.getPath().toString().replace("\"", "''") + "\",";
                    String url = "\"" + download.getUrl().toString().replace("\"", "''") + "\",";
                    String id = "\"" + download.getId().replace("\"", "''") + "\",";
                    String progress = "\"" + download.getPercentString() + "\",";
                    String percent = download.getPercent() + ");";
                    String command = title + state + elapsedTime + downloadSpeed + remainingTime + downloadSize + fileSize + path + url + id + progress + percent;
                    ((ApplicationFrame) app.getFrame()).executeJavaScript(command);
                }
            });
        }

        try {
            JsonObject json = new Gson().fromJson(GsonUtil.getFromURL("https://raw.githubusercontent.com/zyneonstudios/nexus-nex/main/application/index.json"), JsonObject.class).getAsJsonArray("versions").get(0).getAsJsonObject();
            checkVersion(json);
        } catch (Exception ignore) {
        }
    }

    private boolean runUpdateCheck(JsonObject json) {
        if (!ApplicationConfig.test) {
            NexusApplication.getLogger().debug("[RUNNER] Checking for Updates...");
            NexusApplication.getLogger().debug("[RUNNER] Parsed JSON Data...");
            String v = json.get("info").getAsJsonObject().get("version").getAsString();
            NexusApplication.getLogger().debug("[RUNNER] Latest version: " + v + "...");
            NexusApplication.getLogger().debug("[RUNNER] Current version: " + version + "...");
            if (!v.equals(version)) {
                NexusApplication.getLogger().debug("[RUNNER] The application is not up to date!");
                return true;

            }
        }
        return false;
    }

    private void checkVersion(JsonObject json) {
        u++;
        if (u > 120) {
            u = 0;
            if (runUpdateCheck(json)) {
                NexusApplication.getLogger().debug("[RUNNER] Sending notification...");
                //TODO: Application.getFrame().sendNotification("Update available!", "Version " + v + " has been released!", "<a onclick=\"callJavaMethod('button.exit');\" class='button'>Install</a><a onclick=\"callJavaMethod('button.online');\" class='button'>Dynamic update</a>", v, true);
            }
            ;
        }
    }
}