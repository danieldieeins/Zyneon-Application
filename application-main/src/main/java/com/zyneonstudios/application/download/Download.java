package com.zyneonstudios.application.download;

import com.zyneonstudios.application.events.DownloadFinishEvent;
import com.zyneonstudios.application.main.NexusApplication;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

public class Download {

    private DownloadManager.DownloadState state = DownloadManager.DownloadState.WAITING;
    String percentString = "0%";

    private Instant startTime;
    private Instant finishTime = null;
    private long lastBytesRead = 0;
    private int fileSize = 0;

    private final String id;
    private final String name;
    private final Path path;
    private final URL url;

    private boolean finished = false;
    private double percent = 0;

    private DownloadFinishEvent event = null;

    public Download(UUID uuid, URL downloadUrl, Path path) {
        this.id = uuid.toString();
        this.name = this.id;
        this.path = path;
        this.url = downloadUrl;
    }

    public Download(String name, URL downloadUrl, Path path) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.path = path;
        this.url = downloadUrl;
    }

    public Download(UUID uuid, String name, URL downloadUrl, Path path) {
        this.id = uuid.toString();
        this.name = name;
        this.path = path;
        this.url = downloadUrl;
    }

    public void setFinishEvent(DownloadFinishEvent event) {
        this.event = event;
    }

    public boolean start() {
        if(state == DownloadManager.DownloadState.WAITING) {
            state = DownloadManager.DownloadState.RUNNING;
            startTime = Instant.now();
            Instant lastTimeCheck = startTime;

            try {
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                fileSize = connection.getContentLength();
                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    InputStream inputStream = connection.getInputStream();
                    File outputFile = new File(path.toString());
                    FileOutputStream outputStream = new FileOutputStream(outputFile);
                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    long totalBytesRead = 0;
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        totalBytesRead += bytesRead;
                        Instant now = Instant.now();
                        Duration elapsedTime = Duration.between(lastTimeCheck, now);
                        setPercent((totalBytesRead * 100.0) / fileSize);
                        lastBytesRead = totalBytesRead;
                        lastTimeCheck = now;
                        outputStream.write(buffer, 0, bytesRead);
                        if (elapsedTime.getSeconds() >= 1) {

                            totalBytesRead += bytesRead;
                            setPercent((totalBytesRead * 100.0) / fileSize);
                            String s = (int)percent + "%";
                            if(!percentString.equals(s)) {
                                percentString = s;
                            }
                        }
                    }
                    inputStream.close();
                    outputStream.close();
                    setFinished(true);
                    return true;
                }
            } catch (Exception e) {
                NexusApplication.getLogger().err("Couldn't download \""+url+"\" to \""+path.toString()+"\": " + e.getMessage());
            }
        }
        state = DownloadManager.DownloadState.FAILED;
        return false;
    }

    public String getName() {
        return name;
    }

    public Instant getStartTime() {
        return startTime;
    }

    public int getFileSize() {
        return fileSize;
    }

    public long getLastBytesRead() {
        return lastBytesRead;
    }

    public String getPercentString() {
        return percentString;
    }

    public Path getPath() {
        return path;
    }

    public URL getUrl() {
        return url;
    }

    public DownloadManager.DownloadState getState() {
        return state;
    }

    public Duration getElapsedTime() {
        if(finishTime!=null) {
            return Duration.between(startTime, finishTime);
        }
        return Duration.between(startTime, Instant.now());
    }

    public double getSpeedMbps() {
        Duration elapsed = getElapsedTime();
        if (elapsed.getSeconds() == 0) {
            return 0;
        }

        long bytesDownloaded = (long) (percent / 100.0 * fileSize);
        double d = (bytesDownloaded / 1024.0 / 1024.0) / elapsed.getSeconds();
        return Math.round((d * 100) / 100);
    }

    public Duration getEstimatedRemainingTime() {
        double speedMbps = getSpeedMbps();
        if (speedMbps == 0) {
            return Duration.ZERO;
        }

        long remainingBytes = (long) ((100 - percent) / 100.0 * fileSize);
        double remainingSeconds = remainingBytes / 1024.0 / 1024.0 / speedMbps;
        return Duration.ofSeconds((long) remainingSeconds);
    }

    public String getId() {
        return id;
    }

    public UUID getUuid() {
        return UUID.fromString(id);
    }

    public double getPercent() {
        return percent;
    }

    public boolean isFinished() {
        return finished;
    }

    private void setPercent(double percent) {
        this.percent = percent;
        if(percent>100) {
            setFinished(true);
        } else if(percent<0) {
            percent = 0;
        }
    }

    private void setFinished(boolean finished) {
        this.finished = finished;
        if(event!=null) {
            event.execute();
        }
        if(finished) {
            finishTime = Instant.now();
            percent = 100;
            percentString = "100%";
            state = DownloadManager.DownloadState.FINISHED;
        } else {
            percent = -1;
            percentString = "-1%";
            state = DownloadManager.DownloadState.FAILED;
        }
    }
}