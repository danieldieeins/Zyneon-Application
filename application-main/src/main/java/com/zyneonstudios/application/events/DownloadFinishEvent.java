package com.zyneonstudios.application.events;

import com.zyneonstudios.application.download.Download;

import java.util.UUID;

public abstract class DownloadFinishEvent implements Event {

    private final UUID uuid = UUID.randomUUID();
    private final Download download;

    public DownloadFinishEvent(Download download) {
        this.download = download;
    }

    public Download getDownload() {
        return download;
    }

    @Override
    public UUID getUUID() {
        return uuid;
    }

    @Override
    public boolean execute() {
        return onFinish();
    }

    public abstract boolean onFinish();
}