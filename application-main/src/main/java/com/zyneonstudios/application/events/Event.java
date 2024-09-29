package com.zyneonstudios.application.events;

import java.util.UUID;

public interface Event {

    UUID getUUID();

    boolean execute();
}