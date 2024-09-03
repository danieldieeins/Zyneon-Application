package com.zyneonstudios.application.main;

import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ApplicationRunner {

    private ScheduledExecutorService executor;
    private boolean started = false;
    private int u = 119;
    private final NexusApplication app;

    private UUID downloading = null;

    private final String version = ApplicationStorage.getApplicationVersion();
    //TODO private final String motd_id = "";

    public ApplicationRunner(NexusApplication app) {
        this.app = app;
    }

    public ScheduledExecutorService getExecutor() {
        return executor;
    }

    public void start() {
        executor = Executors.newScheduledThreadPool(1);
        executor.scheduleAtFixedRate(this::run, 0, 1, TimeUnit.SECONDS);
    }

    protected void run() {

    }
}